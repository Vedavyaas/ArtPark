package com.pheonix.artpark.service;

import com.pheonix.artpark.repository.ResumeDetailsEntity;
import com.pheonix.artpark.repository.ResumeDetailsRepository;
import com.pheonix.artpark.repository.UserDetailEntity;
import com.pheonix.artpark.repository.UserDetailRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeIngestionService {
    private final UserDetailRepository userDetailRepository;
    private final ResumeDetailsRepository resumeDetailsRepository;

    public ResumeIngestionService(UserDetailRepository userDetailRepository, ResumeDetailsRepository resumeDetailsRepository) {
        this.userDetailRepository = userDetailRepository;
        this.resumeDetailsRepository = resumeDetailsRepository;
    }

    public String ingestResume(String name, MultipartFile file) {
        UserDetailEntity userDetailEntity;
        if (userDetailRepository.existsByUsername(name)) userDetailEntity = userDetailRepository.findByUsername(name);
        else {
            userDetailEntity = new UserDetailEntity(name);
            userDetailRepository.save(userDetailEntity);
        }
        ResumeDetailsEntity resumeDetailsEntity = new ResumeDetailsEntity(userDetailEntity);
        resumeDetailsRepository.save(resumeDetailsEntity);

        try {
            extractDataAsync(file.getBytes(), resumeDetailsEntity.getId());
        } catch (IOException e) {
            return "Failed try again";
        }

        return "Resume uploaded, Check for review";
    }

    @Async
    protected void extractDataAsync(byte[] fileBytes, Long entityId) {
        ResumeDetailsEntity entity = resumeDetailsRepository.findById(entityId).orElse(null);
        if (entity == null) return;

        String fullText = "";
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            fullText = stripper.getText(document);
        } catch (IOException e) {
            fullText = "Error parsing PDF";
        }

        extractSkills(fullText, entity);
        extractExperience(fullText, entity);
    }

    protected void extractExperience(String text, ResumeDetailsEntity entity) {
        String experience = parseSection(text, "EXPERIENCE", "SKILLS", "EDUCATION");

        entity.setExperience(experience);
        entity.setExperienceUpdated(true);
        resumeDetailsRepository.save(entity);
    }

    protected void extractSkills(String text, ResumeDetailsEntity entity) {
        String skills = parseSection(text, "SKILLS", "EXPERIENCE", "PROJECTS");

        entity.setSkills(skills);
        entity.setSkillsUpdated(true);
        resumeDetailsRepository.save(entity);
    }

    private String parseSection(String text, String target, String... delimiters) {
        String lowerText = text.toLowerCase();
        int start = lowerText.indexOf(target.toLowerCase());
        if (start == -1) return "Not found";

        int end = text.length();
        for (String delim : delimiters) {
            int delimPos = lowerText.indexOf(delim.toLowerCase(), start + target.length());
            if (delimPos != -1 && delimPos < end) {
                end = delimPos;
            }
        }
        return text.substring(start + target.length(), end).trim();
    }
}
