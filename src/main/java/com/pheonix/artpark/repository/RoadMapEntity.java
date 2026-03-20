package com.pheonix.artpark.repository;

import jakarta.persistence.*;

@Entity
public class RoadMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ResumeDetailsEntity resumeDetailsEntity;
    @Column(length = 5000)
    private String roadMap;

    public RoadMapEntity() { }
    public RoadMapEntity(ResumeDetailsEntity resumeDetailsEntity, String roadMap) {
        this.resumeDetailsEntity = resumeDetailsEntity;
        this.roadMap = roadMap;
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

    public String getRoadMap() {
        return roadMap;
    }

    public void setRoadMap(String roadMap) {
        this.roadMap = roadMap;
    }
}
