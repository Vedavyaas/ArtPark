package com.pheonix.artpark.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeIngestionService {

    private final RoadMapGeneratorService roadMapGeneratorService;

    public ResumeIngestionService(RoadMapGeneratorService roadMapGeneratorService) {
        this.roadMapGeneratorService = roadMapGeneratorService;
    }

    public String processFilesAndGenerateRoadmap(MultipartFile resumeFile, MultipartFile jdFile) {
        try {
            String resumeText = parsePdf(resumeFile.getBytes());
            String jdText = parsePdf(jdFile.getBytes());
            
            String skills = parseSection(resumeText, "SKILLS", "EXPERIENCE", "PROJECTS", "EDUCATION");
            String experience = parseSection(resumeText, "EXPERIENCE", "SKILLS", "EDUCATION", "CERTIFICATIONS");
            String requirements = parseSection(jdText, "REQUIREMENTS", "QUALIFICATIONS", "RESPONSIBILITIES", "BENEFITS");
            
            if ("Not found".equalsIgnoreCase(requirements)) {
                requirements = jdText.length() > 1500 ? jdText.substring(0, 1500) : jdText;
            }

            return roadMapGeneratorService.getRoadMapInCSV(skills, experience, requirements);

        } catch (IOException e) {
            return "Failed to process files: " + e.getMessage();
        }
    }

    private String parsePdf(byte[] fileBytes) {
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        } catch (IOException e) {
            return "Error parsing PDF content";
        }
    }

    private String parseSection(String text, String target, String... delimiters) {
        String lowerText = text.toLowerCase();
        int start = lowerText.indexOf(target.toLowerCase());

        if (start == -1) return "Not found";

        int contentStart = start + target.length();
        int end = text.length();

        for (String delim : delimiters) {
            int delimPos = lowerText.indexOf(delim.toLowerCase(), contentStart);
            if (delimPos != -1 && delimPos < end) {
                end = delimPos;
            }
        }

        return text.substring(contentStart, end).trim();
    }
}