package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.ActivityComment;
import com.gdufs.demo.entity.AverageScoreEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ActivityApplyDao {
    int insertActivityApply(ActivityApply activityApply);

    List<ActivityApply> queryActivityApply(Map<String, Integer> paraMap);

    List<ActivityApply> queryOfficeApply(Integer admin2Status, Date date);

    List<ActivityApply> queryEnrollApply(Date date);

    List<ActivityApply> queryMyApplyActivity(String username);

    ActivityApply queryActivityApplyById(int activityId);

    int updateApplyStatus(ActivityApply activityApply);

    String getFullImageById(int activityId);

    int insertActivityComment(ActivityComment activityComment);

    List<ActivityComment> queryActivityComment();

    int deleteActivityApply(Integer activityId);

    AverageScoreEntity getActivityAverageScore(Integer activityId);


}
