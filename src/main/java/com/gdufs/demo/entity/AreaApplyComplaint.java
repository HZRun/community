package com.gdufs.demo.entity;

public class AreaApplyComplaint {
    private Integer id;
    private Integer applyId;
    private String complaintReason;
    private Integer scoreChange;
    private String banTime;
    private Integer status;
    private String admin1User;
    private String admin2User;
    private Long updateTime;
    private AreaApply areaApply;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
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


    public AreaApply getAreaApply() {
        return areaApply;
    }

    public void setAreaApply(AreaApply areaApply) {
        this.areaApply = areaApply;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

}
