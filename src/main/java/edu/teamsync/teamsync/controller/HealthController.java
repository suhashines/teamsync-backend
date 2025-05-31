package edu.teamsync.teamsync.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("api/v1/health")

public class HealthController {

    @GetMapping
    public String healthCheck() {
        return "I am healthy";
    }
}
