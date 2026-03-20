package com.pheonix.artpark.repository;

import jakarta.persistence.*;

@Entity
public class JobDescriptorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private ResumeDetailsEntity resumeDetailsEntity;
    private String requirements;
    private boolean updated;

    public JobDescriptorEntity() {
    }

    public JobDescriptorEntity(ResumeDetailsEntity resumeDetailsEntity) {
        this.resumeDetailsEntity = resumeDetailsEntity;
        this.updated = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public ResumeDetailsEntity getResumeDetailsEntity() {
        return resumeDetailsEntity;
    }

    public void setResumeDetailsEntity(ResumeDetailsEntity resumeDetailsEntity) {
        this.resumeDetailsEntity = resumeDetailsEntity;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
