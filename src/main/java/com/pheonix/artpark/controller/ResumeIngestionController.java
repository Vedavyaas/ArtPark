package com.pheonix.artpark.controller;

import com.pheonix.artpark.service.ResumeIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ResumeIngestionController {
    private final ResumeIngestionService resumeIngestionService;

    public ResumeIngestionController(ResumeIngestionService resumeIngestionService) {
        this.resumeIngestionService = resumeIngestionService;
    }

    @PostMapping("/ingest/resume")
    public String ingestResume(@RequestParam String name, @RequestParam MultipartFile file) {
        return resumeIngestionService.ingestResume(name, file);
    }
}
