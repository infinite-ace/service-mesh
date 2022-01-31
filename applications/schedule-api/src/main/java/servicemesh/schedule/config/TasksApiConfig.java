package servicemesh.schedule.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import servicemesh.schedule.api.TaskControllerApi;
import servicemesh.schedule.invoker.ApiClient;

@Configuration
@RequiredArgsConstructor
public class TasksApiConfig {

    @Value("${sdk.basepath.tasks}")
    private String tasksSdkBasePath;

    private final RestTemplate restTemplateSelfSignedCert;

    @Bean
    @Primary
    public ApiClient tasksApiClient() {
        ApiClient apiClient = new ApiClient(restTemplateSelfSignedCert);
        apiClient.setBasePath(tasksSdkBasePath);
        return apiClient;
    }

    @Bean
    @Primary
    public TaskControllerApi tasksApi(ApiClient apiClient) {
        return new TaskControllerApi(apiClient);
    }

}
