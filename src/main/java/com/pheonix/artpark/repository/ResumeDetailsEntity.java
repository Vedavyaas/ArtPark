package com.pheonix.artpark.repository;

import jakarta.persistence.*;

@Entity
public class ResumeDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UserDetailEntity userDetailEntity;
    @Column(length = 10000)
    private String experience;
    @Column(length = 10000)
    private String skills;
    private boolean experienceUpdated;
    private boolean skillsUpdated;

    public ResumeDetailsEntity() {
    }

    public ResumeDetailsEntity(UserDetailEntity userDetailEntity) {
        this.userDetailEntity = userDetailEntity;
        this.experienceUpdated = false;
        this.skillsUpdated = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public UserDetailEntity getUserDetailEntity() {
        return userDetailEntity;
    }

    public void setUserDetailEntity(UserDetailEntity userDetailEntity) {
        this.userDetailEntity = userDetailEntity;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public boolean isExperienceUpdated() {
        return experienceUpdated;
    }

    public void setExperienceUpdated(boolean experienceUpdated) {
        this.experienceUpdated = experienceUpdated;
    }

    public boolean isSkillsUpdated() {
        return skillsUpdated;
    }

    public void setSkillsUpdated(boolean skillsUpdated) {
        this.skillsUpdated = skillsUpdated;
    }
}
