package com.pheonix.artpark.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RoadMapGeneratorService {

    public String getRoadMapInCSV(String skills, String experience, String requirements) {
        String pythonUrl = "http://localhost:8000/generate-roadmap";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> request = new HashMap<>();
        request.put("skills", skills);
        request.put("experience", experience);
        request.put("requirements", requirements);

        try {
            return restTemplate.postForObject(pythonUrl, request, String.class);
        } catch (Exception e) {
            System.err.println("Failed to reach Python ML service: " + e.getMessage());
            return "Failed to reach Python ML service: " + e.getMessage();
        }
    }
}