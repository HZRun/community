package com.gdufs.demo.entity;

public class Gift {
    private Long giftId;
    private String giftName;
    private String introduce;
    private Integer remainNum;     //剩余数量
    private Integer value;         //所需积分
    private String redeemLocation; //兑换地点
    private String imageUrl;

    public Long getGiftId() {
        return giftId;
    }

    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(Integer remainNum) {
        this.remainNum = remainNum;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getRedeemLocation() {
        return redeemLocation;
    }

    public void setRedeemLocation(String redeemLocation) {
        this.redeemLocation = redeemLocation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
