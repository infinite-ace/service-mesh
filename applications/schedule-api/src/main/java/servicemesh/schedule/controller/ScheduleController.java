package servicemesh.schedule.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import servicemesh.schedule.dto.Schedule;
import servicemesh.schedule.model.Task;
import servicemesh.schedule.service.ScheduleService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/schedule", produces = "application/json")
public class ScheduleController {

    private final List<Schedule> schedules = Arrays.asList(
            Schedule.builder().id("1").name("Monday").build(),
            Schedule.builder().id("2").name("Tuesday").build(),
            Schedule.builder().id("3").name("Wednesday").build(),
            Schedule.builder().id("4").name("Thursday").build(),
            Schedule.builder().id("5").name("Friday").build(),
            Schedule.builder().id("6").name("Saturday").build(),
            Schedule.builder().id("7").name("Sunday").build()

    );

    private final Map<String, List<String>> dailySchedule = Map.of(
            "1", Arrays.asList("1", "3", "4"),
            "2", Arrays.asList("1", "3"),
            "3", Arrays.asList("1", "3", "4", "5"),
            "4", Arrays.asList("1", "3"),
            "5", Arrays.asList("1", "3"),
            "6", Arrays.asList("1", "2", "3", "4", "5"),
            "7", Arrays.asList("3")
    );

    @Autowired
    private ScheduleService scheduleService;

    @ResponseBody
    @GetMapping("/all")
    public ResponseEntity<List<Schedule>> getSchedules() {
        log.info("listing schedules");
        return ResponseEntity.accepted().body(listSchedules());
    }

    private List<Schedule> listSchedules() {
        Map<String, Task> tasks = scheduleService.getTasks().stream().collect(Collectors.toMap(
                Task::getId, Function.identity()
        ));
        log.debug("tasks: {}", tasks);
        schedules.stream().forEach(schedule -> {
            List<String> taskIds = dailySchedule.get(schedule.getId());
            List<Task> collectedTasks = getTasks(taskIds, tasks);
            log.debug("collected tasks for schedule {}: {}", schedule.getName(), tasks);
            schedule.setTasks(collectedTasks);
        });
        return schedules;
    }

    private List<Task> getTasks(List<String> ids, Map<String, Task> tasks) {
        List<Task> result = new ArrayList<>();
        for (String id : ids) {
            Task task = tasks.get(id);
            if (Objects.nonNull(task)) {
                result.add(task);
            }
        }
        return result;
    }

}
