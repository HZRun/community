package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityPunishment;
import com.gdufs.demo.entity.AreaApplyComplaint;

import java.util.List;

public interface ActivityPunishmentDao {
    int insertActivityPunishment(Integer activityId, Long updateTime);

    List<ActivityPunishment> queryActiviyPunishment(Integer status, Long queryTime);

    int changeStatus(ActivityPunishment activityPunishment);
}
