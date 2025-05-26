package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.entity.Tasks;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.repository.TaskRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Tasks> getTasksForUser(String userEmail) {
        // Find user by email (extracted from JWT)
        Users user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Long userId = user.getId();

        // Fetch tasks assigned to the user
        List<Tasks> assignedTasks = taskRepository.findByAssignedToId(userId);

        // Fetch tasks from projects the user is a member of
        List<Tasks> projectTasks = taskRepository.findByUserProjects(userId);

        // Combine lists and remove duplicates based on id
        return Stream.concat(assignedTasks.stream(), projectTasks.stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Tasks::getId, task -> task, (existing, replacement) -> existing),
                        map -> new ArrayList<>(map.values())));
    }
}

//package edu.teamsync.teamsync.service;
//
//import edu.teamsync.teamsync.dto.TaskDTO;
//import edu.teamsync.teamsync.entity.Tasks;
//import edu.teamsync.teamsync.entity.Users;
//import edu.teamsync.teamsync.repository.TaskRepository;
//import edu.teamsync.teamsync.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//public class TaskService {
//    @Autowired
//    private TaskRepository taskRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public List<TaskDTO> getTasksForUser(String userEmail) {
//        Users user = userRepository.findByEmail(userEmail);
//        if (user == null) {
//            throw new RuntimeException("User not found");
//        }
//
//        Long userId = user.getId();
//
//        // Fetch tasks assigned to the user and from projects
//        List<Tasks> assignedTasks = taskRepository.findByAssignedToId(userId);
//        List<Tasks> projectTasks = taskRepository.findByUserProjects(userId);
//
//        // Combine and remove duplicates
//        List<Tasks> combinedTasks = Stream.concat(assignedTasks.stream(), projectTasks.stream())
//                .collect(Collectors.collectingAndThen(
//                        Collectors.toMap(Tasks::getId, task -> task, (existing, replacement) -> existing),
//                        map -> new ArrayList<>(map.values())));
//
//        // Convert to DTOs
//        return combinedTasks.stream()
//                .map(TaskDTO::new)
//                .collect(Collectors.toList());
//    }
//}