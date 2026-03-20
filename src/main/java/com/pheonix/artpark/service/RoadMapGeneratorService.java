package com.pheonix.artpark.service;

import com.pheonix.artpark.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RoadMapGeneratorService {
    private final RoadMapRepository roadMapRepository;
    private final ResumeDetailsRepository resumeDetailsRepository;

    public RoadMapGeneratorService(RoadMapRepository roadMapRepository, ResumeDetailsRepository resumeDetailsRepository) {
        this.roadMapRepository = roadMapRepository;
        this.resumeDetailsRepository = resumeDetailsRepository;
    }

    public String getRoadMap(String name) {
        ResumeDetailsEntity resumeDetailsEntity;
        if (resumeDetailsRepository.existsByUserDetailEntity_Username(name)) {
            resumeDetailsEntity = resumeDetailsRepository.findByUserDetailEntity(new UserDetailEntity(name));
        } else return "Username not found";

        RoadMapEntity roadMapEntity;
        if (roadMapRepository.existsByResumeDetailsEntity_UserDetailEntity_Username(name)) {
            roadMapEntity = roadMapRepository.findByResumeDetailsEntity(resumeDetailsEntity);
        } else return "Not updated";

        return roadMapEntity.getRoadMap();
    }

    @Scheduled(fixedDelay = 10_000)
    public void updateRoadmap() {
        int pageSize = 50;
        Pageable pageable = PageRequest.of(0, pageSize);

        while (true) {
            Slice<ResumeDetailsEntity> slice = resumeDetailsRepository
                    .findAllBySkillsUpdatedAndExperienceUpdated(true, true, pageable);

            if (slice.isEmpty()) {
                break;
            }

            for (ResumeDetailsEntity entity : slice) {
                String roadMap = getRoadMapInCSV(entity.getSkills(), entity.getExperience());

                RoadMapEntity roadMapEntity = new RoadMapEntity(entity, roadMap);
                roadMapRepository.save(roadMapEntity);
                entity.setSkillsUpdated(false);
                resumeDetailsRepository.save(entity);
            }
            if (!slice.hasNext()) {
                break;
            }
        }
    }

    public String getRoadMapInCSV(String skills, String experience) {
        String pythonUrl = "http://localhost:8000/generate-roadmap";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> request = new HashMap<>();
        request.put("skills", skills);
        request.put("experience", experience);

        try {
            return restTemplate.postForObject(pythonUrl, request, String.class);
        } catch (Exception e) {
            System.err.println("Failed to reach Python ML service: " + e.getMessage());
            return null;
        }
    }
}