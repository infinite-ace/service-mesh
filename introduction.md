# An Introduction to Service Mesh

## Simple service: schedule-api
<hr/>
<br/>

Consider `schedule-api` <br/>
It's a service with the tasks scheduled for the weekdays.

```
+--------------+
| schedule-api |
|              |
+--------------+
```
<br/>

## A simple API: schedule-api
<hr/>
<br/>

For `schedule-api` to get the tasks associated to the days, it needs to make a call to `tasks-api`

```
+--------------+     +-----------+
| schedule-api +---->+ tasks-api |
|              |     |           |
+--------------+     +-----------+

```

A schedule consist of `id` and `name` etc, and a list of `tasks`. <br/>

## Traffic flow
<hr/>
<br/>
A single `GET` request to the `schedule-api` will get all the tasks
from the `tasks-api` service <br/>

This will result in network calls between the `schedule-api` and the `tasks-api`<br/>

<br/>

## Adding an Ingress Controller

Adding an ingress controller allows us to route all our traffic. </br>
We setup a `host` file with entry `127.0.0.1  servicemesh.demo`
And `port-forward` to the `ingress-controller`


```
servicemesh.demo/swagger-ui.html


                              servicemesh.demo/swagger-ui.html           +--------------+
                                        +------------------------------> | schedule-api |
                                        |                                |              |
servicemesh.demo/swagger-ui.html +------+------------+                   +--------------+
      +------------------------->+ingress-nginx      |
                                 |Ingress controller |
                                 +------+------------+                   +---------------+
                                        |                                |   tasks-api   +
                                        +------------------------------> |               |
                                                                         +---------------+

```
<br/>

## Run the apps: Docker
<hr/>
<br/>
There is a `docker-compose.yaml`  in this directory. <br/>
Change your terminal to this folder and run:

```
docker-compose build

docker-compose up

```

You can access the app on `http://localhost/swagger-ui.html`

<br/>

## Run the apps: Kubernetes
<hr/>
<br/>

Create a cluster with [kind](https://kind.sigs.k8s.io/docs/user/quick-start/)

```
kind create cluster --name servicemesh --image kindest/node:v1.18.4
```
<br/>

### Deploy schedule-api

<hr/>
<br/>

```
cd ./kubernetes/servicemesh/

kubectl apply -f applications/schedule-api/deploy.yaml
kubectl port-forward svc/schedule-api 80:80

```

You can make a call to the  endpoint at `http://localhost/swagger-ui.html` <br/>

<br/>

### Deploy tasks-api

<hr/>
<br/>

```
cd ./kubernetes/servicemesh/

kubectl apply -f applications/tasks-api/deploy.yaml
kubectl port-forward svc/playlists-api 81:80

```

You should see empty playlists page at `http://localhost/` <br/>
Playlists are empty because it needs the `videos-api` to get video data <br/>

<br/>
You should now see the complete architecture in the browser <br/>
