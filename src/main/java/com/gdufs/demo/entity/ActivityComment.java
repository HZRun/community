package com.gdufs.demo.entity;

public class ActivityComment {
    private ActivityApply activityApply;

    private Integer commentId;

    private Integer activityId;

    private String username;

    private Integer contentScore;

    private Integer organizeScore;

    private Integer useScore;

    private String content;

    private Long createTime;

    public ActivityApply getActivityApply() {
        return activityApply;
    }

    public void setActivityApply(ActivityApply activityApply) {
        this.activityApply = activityApply;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getContentScore() {
        return contentScore;
    }

    public void setContentScore(Integer contentScore) {
        this.contentScore = contentScore;
    }

    public Integer getOrganizeScore() {
        return organizeScore;
    }

    public void setOrganizeScore(Integer organizeScore) {
        this.organizeScore = organizeScore;
    }

    public Integer getUseScore() {
        return useScore;
    }

    public void setUseScore(Integer useScore) {
        this.useScore = useScore;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
