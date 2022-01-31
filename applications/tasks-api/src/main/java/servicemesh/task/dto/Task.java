package servicemesh.task.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Task {

    private String id;

    private String name;

}
