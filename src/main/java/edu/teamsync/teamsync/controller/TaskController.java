package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.entity.Tasks;
import edu.teamsync.teamsync.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Tasks>> getTasks() {
        // Get email from SecurityContext (already validated by JwtValidator)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        List<Tasks> tasks = taskService.getTasksForUser(email);
        return ResponseEntity.ok(tasks);
    }
}

//import edu.teamsync.teamsync.dto.TaskDTO;
//import edu.teamsync.teamsync.service.TaskService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/tasks")
//public class TaskController {
//
//    @Autowired
//    private TaskService taskService;
//
//    @GetMapping
//    public ResponseEntity<List<TaskDTO>> getTasks() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//
//        List<TaskDTO> tasks = taskService.getTasksForUser(email);
//        return ResponseEntity.ok(tasks);
//    }
//}