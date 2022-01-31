package servicemesh.task.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import servicemesh.task.dto.Task;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/tasks", produces = "application/json")
public class TaskController {

    private final List<Task> tasks = Arrays.asList(
            Task.builder().id("1").name("washing").build(),
            Task.builder().id("2").name("hoovering").build(),
            Task.builder().id("3").name("dishwashing").build(),
            Task.builder().id("4").name("cooking").build(),
            Task.builder().id("5").name("do the shopping").build(),
            Task.builder().id("6").name("learning").build()
    );

    @ResponseBody
    @GetMapping("/")
    public ResponseEntity<List<Task>> getTasks() {
        log.info("listing tasks");
        return ResponseEntity.accepted().body(tasks);
    }

}
