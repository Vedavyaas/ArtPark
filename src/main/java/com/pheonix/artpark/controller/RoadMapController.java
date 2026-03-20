package com.pheonix.artpark.controller;

import com.pheonix.artpark.service.RoadMapGeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoadMapController {
    private final RoadMapGeneratorService roadMapRepository;

    public RoadMapController(RoadMapGeneratorService roadMapGeneratorService) {
        this.roadMapRepository = roadMapGeneratorService;
    }

    @GetMapping("/get/roadmap")
    public String getRoadmap(@RequestParam String name) {
        String result = roadMapRepository.getRoadMap(name);
        if (result == null || result.equals("Username not found") || result.equals("Not updated")) {
            return "Phase,Title,Description,Duration\n" +
                   "1,Foundation,Master Java 17 and Core OOP principles,2 weeks\n" +
                   "2,Spring ecosystem,Learn Spring Boot, Dependency Injection, and Context,3 weeks\n" +
                   "3,Data Access,Delve into JPA, Hibernate, and advanced SQL operations,2 weeks\n" +
                   "4,Security,Implement JWT Auth and RBAC with Spring Security,2 weeks\n" +
                   "5,Cloud Deployment,Dockerize and deploy microservices on AWS/Vercel,3 weeks";
        }
        return result;
    }
}
