package servicemesh.schedule.dto;

import lombok.Builder;
import lombok.Data;
import servicemesh.schedule.model.Task;

import java.util.List;

@Data
@Builder
public class Schedule {

    private String id;

    private String name;

    private List<Task> tasks;

}
