package com.gdufs.demo.service.impl;

import com.gdufs.demo.dao.ActivityPunishmentDao;
import com.gdufs.demo.entity.ActivityPunishment;
import com.gdufs.demo.entity.AreaApplyComplaint;
import com.gdufs.demo.service.ActivityPunishmentService;
import com.gdufs.demo.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityPunishmentServiceImpl implements ActivityPunishmentService {
    @Autowired
    private ActivityPunishmentDao activityPunishmentDao;

    @Override
    public Boolean addActivityPunishment(Integer activityId) {
        try {
            Long updateTime = Func.getIntTime();
            int effectNum = activityPunishmentDao.insertActivityPunishment(activityId, updateTime);
            if (effectNum > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("插入活动惩罚失败");
        }
    }

    @Override
    public List<ActivityPunishment> queryActivityPunishment(Integer status, Long queryTime) {
        return activityPunishmentDao.queryActiviyPunishment(status, queryTime);
    }

    @Override
    public Boolean changeStatus(ActivityPunishment activityPunishment) {
        try {
            int effctNum = activityPunishmentDao.changeStatus(activityPunishment);
            if (effctNum > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("执行活动惩罚措施失败");
        }
    }


}
