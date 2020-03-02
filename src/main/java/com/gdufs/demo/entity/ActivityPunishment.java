package com.gdufs.demo.entity;

public class ActivityPunishment {
    private Integer id;
    private Integer activityId;
    private String complaintReason;
    private Integer scoreChange;
    private String banTime;
    private Integer status;
    private String admin1User;
    private String admin2User;
    private Long updateTime;
    private ActivityApply activityApply;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getComplaintReason() {
        return complaintReason;
    }

    public void setComplaintReason(String complaintReason) {
        this.complaintReason = complaintReason;
    }

    public Integer getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(Integer scoreChange) {
        this.scoreChange = scoreChange;
    }

    public String getBanTime() {
        return banTime;
    }

    public void setBanTime(String banTime) {
        this.banTime = banTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAdmin1User() {
        return admin1User;
    }

    public void setAdmin1User(String admin1User) {
        this.admin1User = admin1User;
    }

    public String getAdmin2User() {
        return admin2User;
    }

    public void setAdmin2User(String admin2User) {
        this.admin2User = admin2User;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public ActivityApply getActivityApply() {
        return activityApply;
    }

    public void setActivityApply(ActivityApply activityApply) {
        this.activityApply = activityApply;
    }
}
