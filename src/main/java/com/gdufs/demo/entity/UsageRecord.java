package com.gdufs.demo.entity;

public class UsageRecord {
    private Integer id;
    private Integer areaId;
    private String day;
    private Integer applyId;
    private Integer type;
    private Integer startTimeIndex;
    private Integer endTimeIndex;
    private String adminUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStartTimeIndex() {
        return startTimeIndex;
    }

    public void setStartTimeIndex(Integer startTimeIndex) {
        this.startTimeIndex = startTimeIndex;
    }

    public Integer getEndTimeIndex() {
        return endTimeIndex;
    }

    public void setEndTimeIndex(Integer endTimeIndex) {
        this.endTimeIndex = endTimeIndex;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }
}
