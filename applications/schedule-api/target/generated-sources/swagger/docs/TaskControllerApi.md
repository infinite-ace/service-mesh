# TaskControllerApi

All URIs are relative to *//localhost:10011/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getTasksUsingGET**](TaskControllerApi.md#getTasksUsingGET) | **GET** /tasks/ | getTasks

<a name="getTasksUsingGET"></a>
# **getTasksUsingGET**
> List&lt;Task&gt; getTasksUsingGET()

getTasks

### Example
```java
// Import classes:
//import servicemesh.schedule.invoker.ApiException;
//import servicemesh.schedule.api.TaskControllerApi;


TaskControllerApi apiInstance = new TaskControllerApi();
try {
    List<Task> result = apiInstance.getTasksUsingGET();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TaskControllerApi#getTasksUsingGET");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;Task&gt;**](Task.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

