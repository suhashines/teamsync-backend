//
//package edu.teamsync.teamsync.controller;
//
//import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
//import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
//import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
//import edu.teamsync.teamsync.exception.http.NotFoundException;
//import edu.teamsync.teamsync.response.SuccessResponse;
//import edu.teamsync.teamsync.service.TaskService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/tasks")
//@RequiredArgsConstructor
//public class TaskController {
//
//    private final TaskService tasksService;
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SuccessResponse<TaskResponseDTO>> getTaskById(@PathVariable Long id) {
//        TaskResponseDTO task = tasksService.getTaskById(id);
//
//        SuccessResponse<TaskResponseDTO> response = SuccessResponse.<TaskResponseDTO>builder()
//                .code(HttpStatus.OK.value())
//                .status(HttpStatus.OK)
//                .message("Task retrieved successfully")
//                .data(task)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping
//    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getAllTasks() {
//        List<TaskResponseDTO> tasks = tasksService.getAllTasks();
//
//        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
//                .code(HttpStatus.OK.value())
//                .status(HttpStatus.OK)
//                .message("All tasks retrieved successfully")
//                .data(tasks)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/project/{projectId}")
//    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getTasksByProjectId(@PathVariable Long projectId) {
//        List<TaskResponseDTO> tasks = tasksService.getTasksByProjectId(projectId);
//
//        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
//                .code(HttpStatus.OK.value())
//                .status(HttpStatus.OK)
//                .message("Project tasks retrieved successfully")
//                .data(tasks)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping
//    public ResponseEntity<SuccessResponse<Void>> createTask(@Valid @RequestBody TaskCreationDTO createDto) {
//        tasksService.createTask(createDto);
//
//        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
//                .code(HttpStatus.CREATED.value())
//                .status(HttpStatus.CREATED)
//                .message("Task created successfully")
////                .data(createdTask)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<SuccessResponse<Void>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO updateDto) {
//        tasksService.updateTask(id, updateDto);
//
//        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
//                .code(HttpStatus.OK.value())
//                .status(HttpStatus.OK)
//                .message("Task updated successfully")
////                .data(updatedTask)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<SuccessResponse<Void>> deleteTask(@PathVariable Long id) {
//        tasksService.deleteTask(id);
//
//        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
//                .code(HttpStatus.NO_CONTENT.value())
//                .status(HttpStatus.OK)
//                .message("Task deleted successfully")
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//}
package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.authorization.ProjectAuthorizationService;
import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService tasksService;
    private final ProjectAuthorizationService authorizationService;

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<TaskResponseDTO>> getTaskById(@PathVariable Long id) {
        TaskResponseDTO task = tasksService.getTaskById(id);

        SuccessResponse<TaskResponseDTO> response = SuccessResponse.<TaskResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Task retrieved successfully")
                .data(task)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getAllTasks() {
        List<TaskResponseDTO> tasks = tasksService.getAllTasks();

        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("All tasks retrieved successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getTasksByProjectId(@PathVariable Long projectId) {
        List<TaskResponseDTO> tasks = tasksService.getTasksByProjectId(projectId);

        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Project tasks retrieved successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createTask(@Valid @RequestBody TaskCreationDTO createDto) {
        // Check if user is admin/owner of the project before creating task
        authorizationService.requireProjectAdminOrOwner(createDto.getProjectId());

        tasksService.createTask(createDto);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Task created successfully")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO updateDto) {
        // Get task to find its project ID, then check authorization
//        TaskResponseDTO task = tasksService.getTaskById(id);
//        authorizationService.requireProjectAdminOrOwner(task.getProjectId());

        tasksService.updateTask(id, updateDto);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Task updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteTask(@PathVariable Long id) {
        // Get task to find its project ID, then check authorization
        TaskResponseDTO task = tasksService.getTaskById(id);
        authorizationService.requireProjectAdminOrOwner(task.getProjectId());

        tasksService.deleteTask(id);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Task deleted successfully")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}