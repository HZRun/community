package com.gdufs.demo.service;

import com.gdufs.demo.entity.ActivityPunishment;
import com.gdufs.demo.entity.AreaApplyComplaint;

import java.util.List;

public interface ActivityPunishmentService {
    Boolean addActivityPunishment(Integer applyId);

    List<ActivityPunishment> queryActivityPunishment(Integer status, Long queryTime);

    Boolean changeStatus(ActivityPunishment activityPunishment);
}
