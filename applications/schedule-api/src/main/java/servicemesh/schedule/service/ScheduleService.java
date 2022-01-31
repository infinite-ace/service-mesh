package servicemesh.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import servicemesh.schedule.api.TaskControllerApi;
import servicemesh.schedule.model.Task;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private TaskControllerApi taskControllerApi;

    public List<Task> getTasks() {
        return taskControllerApi.getTasksUsingGET();
    }

}
