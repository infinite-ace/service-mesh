# Introduction to Istio

## Dependencies

brew install docker

brew install minikube

brew install kubectl

brew install kind

## Make sure that the docker images will be available for K8s

eval $(minikube docker-env)

## Build our applications

docker-compose build

docker-compose up

We can check the swagger UI of the tasks service at http://localhost/swagger-ui.html

We can now stop our applications:

docker-compose down

## We need a Kubernetes cluster

Lets create a Kubernetes cluster to play with using [kind](https://kind.sigs.k8s.io/docs/user/quick-start/)

```
kind create cluster --name istio --image kindest/node:v1.23.3
```


## Deploy our microservices

```
# ingress controller
kubectl create ns ingress-nginx
kubectl apply -f applications/ingress-nginx/

# applications
kubectl apply -f applications/tasks-api/
kubectl apply -f applications/schedule-api/
```

## Make sure our applications are running

```
kubectl get pods
NAME                            READY   STATUS    RESTARTS   AGE
tasks-api-d7f64c9c6-rfhdg       1/1     Running   0          2m19s
schedule-api-67d75dc7f4-p8wk5   1/1     Running   0          2m19s

```

## Make sure our ingress controller is running

```
kubectl -n ingress-nginx get pods
NAME                                        READY   STATUS    RESTARTS   AGE
nginx-ingress-controller-6fbb446cff-8fwxz   1/1     Running   0          2m38s
nginx-ingress-controller-6fbb446cff-zbw7x   1/1     Running   0          2m38s

```

We'll need a fake DNS name `servicemesh.demo` <br/>
Let's fake one by adding the following entry in our hosts (`C:\Windows\System32\drivers\etc\hosts`
on Windows or type `sudo nano /etc/hosts` in the terminal on Mac) file: <br/>

```
127.0.0.1  servicemesh.demo

```

## Let's access our applications via Ingress

```
kubectl -n ingress-nginx port-forward deploy/nginx-ingress-controller 80
```

## Access our application in the browser

We should be able to access our swagger UI under `http://servicemesh.demo/swagger-ui.html`

<br/>
<hr/>

# Getting Started with Istio

## Get a container to work in
<br/>
Run a small `alpine linux` container where we can install and play with `istio`: <br/>


```
docker run -it --rm -v ${HOME}:/root/ -v ${PWD}:/work -w /work --net host alpine sh

# install curl & kubectl
apk add --no-cache curl nano
curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
chmod +x ./kubectl
mv ./kubectl /usr/local/bin/kubectl
export KUBE_EDITOR="nano"

#test cluster access:
/work # kubectl get nodes
NAME                    STATUS   ROLES    AGE   VERSION
istio-control-plane   Ready    master   26m   v1.18.4

```

## Install Istio CLI

```
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.6.12 TARGET_ARCH=x86_64 sh -

mv istio-1.6.12/bin/istioctl /usr/local/bin/
chmod +x /usr/local/bin/istioctl
mv istio-1.6.12 /tmp/

```

## Pre flight checks

Istio has a great capability to check compatibility with the target cluster <br/>

```
istioctl x precheck

```

## Istio Profiles

https://istio.io/latest/docs/setup/additional-setup/config-profiles/

```
istioctl profile list

istioctl install --set profile=default

kubectl -n istio-system get pods

istioctl proxy-status

```

# Mesh our services

There are 2 ways to mesh:

1) Automated Injection:

You can set the `istio-injection=enabled` label on a namespace to have the istio sidecar automatically injected into any pod that gets created in the labelled namespace

This is a more permanent solution:
Pods will need to be recreated for injection to occur

```
kubectl label namespace/default istio-injection=enabled

# restart all pods to get sidecar injected
kubectl delete pods --all
```

2) Manual Injection:
This may only be temporary as your CI/CD system may roll out the previous YAML.
You may want to add this command to your CI/CD to keep only certain deployments part of the mesh.

```
kubectl get deploy
NAME            READY   UP-TO-DATE   AVAILABLE   AGE
schedule-api    1/1     1            1           8h
tasks-api       1/1     1            1           8h

# Lets manually inject istio sidecar into our Ingress Controller:

kubectl -n ingress-nginx get deploy nginx-ingress-controller  -o yaml | istioctl kube-inject -f - | kubectl apply -f -

# You can manually inject istio sidecar to every deployment like this:

kubectl get deploy schedule-api -o yaml | istioctl kube-inject -f - | kubectl apply -f -
kubectl get deploy tasks-api -o yaml | istioctl kube-inject -f - | kubectl apply -f -


```

# TCP \ HTTP traffic

Let's run a `curl` loop to generate some traffic to our site </br>
We'll make a call to `/swagger-ui.html` and to simulate the browser making a call to get the schedules

```
While ($true) { curl -UseBasicParsing http://servicemesh.demo/schedule/all; Start-Sleep -Seconds 1;}
```


# Observability


## Grafana

```
kubectl apply -n istio-system -f /tmp/istio-1.6.12/samples/addons/grafana.yaml
```

We can see the components in the `istio-system` namespace:
```
kubectl -n istio-system get pods
```

Access grafana dashboards :

```
kubectl -n istio-system port-forward svc/grafana 3000
```

## Kiali

`NOTE: this may fail because CRDs need to generate, if so, just rerun the command:`

```
kubectl apply -f /tmp/istio-1.6.12/samples/addons/kiali.yaml

kubectl -n istio-system get pods
kubectl -n istio-system port-forward svc/kiali 20001
```

# Virtual Services

## Auto Retry

Let's add a fault in the `schedule-api` by setting `env` variable `FLAKY=true`

```
kubectl edit deploy schedule-api
```

```
kubectl apply -f kubernetes/servicemesh/istio/retries/schedule-api.yaml
```

We can describe pods using `istioctl`

```
# istioctl x describe pod <schedule-api-POD-NAME>

istioctl x describe pod schedule-api-584768f497-jjrqd
Pod: schedule-api-584768f497-jjrqd
   Pod Ports: 10010 (schedule-api), 15090 (istio-proxy)
Suggestion: add 'version' label to pod for Istio telemetry.
--------------------
Service: schedule-api
   Port: http 10010/HTTP targets pod port 10010
VirtualService: schedule-api
   1 HTTP route(s)
```

Analyse our namespace:

```
istioctl analyze --namespace default
```

## Traffic Splits

Let's deploy V2 of our application which has a header that's under development

```
kubectl apply -f kubernetes/servicemesh/istio/traffic-splits/schedule-api-v2.yaml

# we can see v2 pods
kubectl get pods

```

Let's send 50% of traffic to V1 and 50% to V2 by using a `VirtualService`

```
kubectl apply -f kubernetes/servicemesh/istio/traffic-splits/schedule-api.yaml
```

## Canary Deployments

Traffic splits has its uses, but sometimes we may want to route traffic to other  <br/>
parts of the system using feature toggles, for example, setting a `cookie`<br/>
<br/>
Let's send all users that have the cookie value `version=v2` to V2 of our `schedule-api`.

```
kubectl apply -f kubernetes/servicemesh/istio/canary/schedule-api.yaml
```

We can confirm this works, by setting the cookie value `version=v2` followed by accessing https://servicemesh.demo/swagger-ui.html on a browser page <br/>
