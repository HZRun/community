package com.gdufs.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class AreaApply implements Serializable {
    private Integer applyId;
    private Date startTime;
    private Date endTime;
    private String applyUser;
    private String sponsor;
    private String introduce;
    private Integer applyArea;
    private String areaName;
    private Integer admin1Status;
    private String admin1Advice;
    private Integer admin2Status;
    private String admin2Advice;
    private String admin1Name;
    private String admin2Name;
    private Integer applyStatus;
    private Long createTime;
    private Date enrollTime;
    private UserBase user;

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public Date getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(Date enrollTime) {
        this.enrollTime = enrollTime;
    }

    public UserBase getUser() {
        return user;
    }

    public void setUser(UserBase user) {
        this.user = user;
    }
}
