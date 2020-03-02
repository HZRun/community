package com.gdufs.demo.service.impl;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.gdufs.demo.dao.ActivityApplyDao;
import com.gdufs.demo.dao.ActivityUserDao;
import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.ActivityComment;
import com.gdufs.demo.entity.AverageScoreEntity;
import com.gdufs.demo.entity.Relation;
import com.gdufs.demo.handler.StatusErrorException;
import com.gdufs.demo.service.ActivityApplyService;
import com.gdufs.demo.service.UserService;
import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Constants;
import com.gdufs.demo.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ActivityApplyServiceImpl implements ActivityApplyService {
    @Autowired
    private ActivityApplyDao activityApplyDao;
    @Autowired
    private ActivityUserDao activityUserDao;
    @Autowired
    private Func func;
    @Autowired
    private UserService userService;

    @Override
    public Boolean applyActivity(ActivityApply activityApply) {
        if (activityApply.getStartTime() != null && activityApply.getEndTime() != null) {
            try {
                activityApply.setCreateTime(new Date().getTime() / 1000);
                int effectNum = activityApplyDao.insertActivityApply(activityApply);
                if (effectNum > 0) {
                    return true;
                } else {
                    throw new RuntimeException("活动申请提交失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("失败" + e.getMessage());
            }
        } else {
            throw new RuntimeException("活动开始和结束时间不能为空");
        }
    }

    @Override
    public List<ActivityApply> getActivityApplyList(Map<String, Integer> paraMap) {
        return activityApplyDao.queryActivityApply(paraMap);
    }

    @Override
    public List<ActivityApply> queryMyApplyActivity(String username) {
        return activityApplyDao.queryMyApplyActivity(username);
    }

    @Override
    public List<ActivityApply> queryOfficeApply(Integer admin2Status) {
        Date nowTime = new Date();
        return activityApplyDao.queryOfficeApply(admin2Status, nowTime);
    }

    @Override
    public List<ActivityApply> queryEnrollApply() {
        Date nowTime = new Date();
        return activityApplyDao.queryEnrollApply(nowTime);
    }


    @Override
    public ActivityApply getActivityApplyById(Integer activityId) {
        if (activityId != null) {
            String key = "activityApply:" + activityId;
            if (CacheUtils.isCached(key)) { //有缓存直接拿
                return CacheUtils.getBean(key, ActivityApply.class);
            }
            ActivityApply activityApply = activityApplyDao.queryActivityApplyById(activityId);
            if (activityApply != null) {
                activityApply.setAreaName(func.areaIdToName(activityApply.getApplyArea()));
                activityApply.setUser(userService.queryUser(activityApply.getApplyUser()));
                if (activityApply.getThumbnail().length() > 0) {
                    activityApply.setThumbnail(Constants.PHOTOROOT + activityApply.getThumbnail());
                } else {
                    activityApply.setThumbnail(Constants.DEFAULT_IMG_PATH);
                }
                CacheUtils.saveBean(key, activityApply, Constants.WEEK_ONE);
                return activityApply;
            } else throw new RuntimeException("没有查询到数据");
        } else throw new RuntimeException("请求信息缺失");
    }

    @Override
    public Boolean changeStatus(ActivityApply activityApply) {
        if (activityApply.getActivityId() != null) {
            int effectNum = activityApplyDao.updateApplyStatus(activityApply);
            if (effectNum > 0) {
                String key = "activityApply:" + activityApply.getActivityId();
                if (CacheUtils.isCached(key)) {
                    CacheUtils.delKey(key);
                }
                return true;
            } else return false;
        } else throw new RuntimeException("请求参数出错");
    }

    @Override
    public String getActivityImage(Integer activityId) {
        String fullImage = activityApplyDao.getFullImageById(activityId);
        if (fullImage.length() > 0) {
            return Constants.PHOTOROOT + fullImage;
        } else {
            return Constants.DEFAULT_IMG_PATH;
        }
    }

    @Override
    public Boolean insertActivityComment(ActivityComment activityComment) {
        try {
            if (activityApplyDao.insertActivityComment(activityComment) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("已评论");
        }
    }

    @Override
    public List<ActivityComment> getActivityComment() {
        return activityApplyDao.queryActivityComment();
    }

    @Override
    public Boolean userJoinActivity(Relation relation) {
        try {
            int effectNum = activityUserDao.insertActivityUser(relation);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("报名活动失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("用户已经报名该活动");
        }

    }

    @Override
    public List<Relation> getActivityUserById(Integer activityId) {
        return activityUserDao.queryActivityUserByApplyId(activityId);
    }

    @Override
    public List<ActivityApply> getActivityByUsername(String username) {
        return activityUserDao.queryActivityUserByUsername(username);
    }

    @Override
    public Boolean deleteActivityApply(Integer activityId) {
        int effectNum = activityApplyDao.deleteActivityApply(activityId);
        if (effectNum > 0) {
            return true;
        } else {
            throw new RuntimeException("取消活动申请失败");
        }
    }

    @Override
    public Boolean deleteActivityUser(Integer activityId, String username) {
        try {
            int effectNum = activityUserDao.deleteActivityUser(username, activityId);
            if (effectNum > 0) {
                return true;
            } else {
                throw new StatusErrorException(210, "用户未报名该活动");
            }
        } catch (Exception e) {
            throw new StatusErrorException(210, "用户未报名该活动");
        }

    }

    @Override
    public Boolean queryActivityUserExist(Integer activityId, String username) {
        Relation relation = activityUserDao.queryActivityUserExist(username, activityId);
        if (relation == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public AverageScoreEntity getAverageScore(Integer activityId) {
        return activityApplyDao.getActivityAverageScore(activityId);
    }
}
