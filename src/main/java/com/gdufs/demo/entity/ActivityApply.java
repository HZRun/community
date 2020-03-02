package com.gdufs.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ActivityApply {
    private Integer activityId;
    private Date startTime;
    private Date endTime;
    private String applyUser;
    private UserBase user;
    private String sponsor;
    private String title;
    private String introduce;
    private Integer applyArea;
    private String areaName;
    private Integer membersLess;
    private Integer admin1Status;
    private String admin1Advice;
    private Integer admin2Status;
    private String admin2Advice;
    private String admin1Name;
    private String admin2Name;
    private Integer activityStatus;
    private String thumbnail;
    private String fullImage;
    private Long createTime;
    private Date enrollTime;

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public UserBase getUser() {
        return user;
    }

    public void setUser(UserBase user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getApplyArea() {
        return applyArea;
    }

    public void setApplyArea(Integer applyArea) {
        this.applyArea = applyArea;
    }

    public Integer getMembersLess() {
        return membersLess;
    }

    public void setMembersLess(Integer membersLess) {
        this.membersLess = membersLess;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getAdmin1Status() {
        return admin1Status;
    }

    public void setAdmin1Status(Integer admin1Status) {
        this.admin1Status = admin1Status;
    }

    public String getAdmin1Advice() {
        return admin1Advice;
    }

    public void setAdmin1Advice(String admin1Advice) {
        this.admin1Advice = admin1Advice;
    }

    public Integer getAdmin2Status() {
        return admin2Status;
    }

    public void setAdmin2Status(Integer admin2Status) {
        this.admin2Status = admin2Status;
    }

    public String getAdmin2Advice() {
        return admin2Advice;
    }

    public void setAdmin2Advice(String admin2Advice) {
        this.admin2Advice = admin2Advice;
    }

    public String getAdmin1Name() {
        return admin1Name;
    }

    public void setAdmin1Name(String admin1Name) {
        this.admin1Name = admin1Name;
    }

    public String getAdmin2Name() {
        return admin2Name;
    }

    public void setAdmin2Name(String admin2Name) {
        this.admin2Name = admin2Name;
    }

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFullImage() {
        return fullImage;
    }

    public void setFullImage(String fullImage) {
        this.fullImage = fullImage;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    public Date getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(Date enrollTime) {
        this.enrollTime = enrollTime;
    }

}
