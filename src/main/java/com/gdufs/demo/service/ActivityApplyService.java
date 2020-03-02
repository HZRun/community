package com.gdufs.demo.service;

import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.ActivityComment;
import com.gdufs.demo.entity.AverageScoreEntity;
import com.gdufs.demo.entity.Relation;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.Map;

public interface ActivityApplyService {

    /**
     * 申请活动服务
     *
     * @param activityApply
     * @return
     */
    Boolean applyActivity(ActivityApply activityApply);

    /**
     * 根据条件获得活动申请列表
     *
     * @param paraMap
     * @return
     */
    List<ActivityApply> getActivityApplyList(Map<String, Integer> paraMap);

    List<ActivityApply> queryMyApplyActivity(String username);

    List<ActivityApply> queryOfficeApply(Integer admin2Status);

    List<ActivityApply> queryEnrollApply();

    ActivityApply getActivityApplyById(Integer activityId);

    Boolean changeStatus(ActivityApply activityApply);

    String getActivityImage(Integer activityId);

    Boolean insertActivityComment(ActivityComment activityComment);

    List<ActivityComment> getActivityComment();

    Boolean userJoinActivity(Relation relation);

    List<Relation> getActivityUserById(Integer activityId);

    List<ActivityApply> getActivityByUsername(String username);

    Boolean deleteActivityApply(Integer activityId);

    Boolean deleteActivityUser(Integer activityId, String username);

    Boolean queryActivityUserExist(Integer activityId, String username);

    AverageScoreEntity getAverageScore(Integer activityId);

}
