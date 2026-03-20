package com.pheonix.artpark.controller;

import com.pheonix.artpark.service.ResumeIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class RoadMapController {
    private final ResumeIngestionService resumeIngestionService;

    public RoadMapController(ResumeIngestionService resumeIngestionService) {
        this.resumeIngestionService = resumeIngestionService;
    }

    @PostMapping("/process")
    public String generateRoadmap(@RequestParam MultipartFile resumeFile, @RequestParam MultipartFile jdFile) {
        return resumeIngestionService.processFilesAndGenerateRoadmap(resumeFile, jdFile);
    }
}
