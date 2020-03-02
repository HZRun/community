package com.gdufs.demo.entity;

public class ApplyComment {
    private AreaApply areaApply;

    private Integer commentId;

    private Integer applyId;

    private String username;

    private Integer contentScore;

    private Integer organizeScore;

    private Integer useScore;

    private String content;

    private Long createTime;

    public AreaApply getAreaApply() {
        return areaApply;
    }

    public void setAreaApply(AreaApply areaApply) {
        this.areaApply = areaApply;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
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
