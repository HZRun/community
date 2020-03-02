package com.gdufs.demo.service.impl;

import com.gdufs.demo.dao.ApplyUserDao;
import com.gdufs.demo.dao.AreaApplyDao;
import com.gdufs.demo.entity.*;
import com.gdufs.demo.handler.StatusErrorException;
import com.gdufs.demo.service.AreaApplyService;
import com.gdufs.demo.service.UserService;
import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Constants;
import com.gdufs.demo.utils.Func;
import io.netty.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AreaApplyServiceImpl implements AreaApplyService {
    @Autowired
    private AreaApplyDao areaApplyDao;
    @Autowired
    private ApplyUserDao applyUserDao;
    @Autowired
    private Func func;
    @Autowired
    private UserService userService;

    @Override
    public Boolean applyArea(AreaApply areaApply) {
        if (areaApply.getStartTime() != null && areaApply.getEndTime() != null) {
            try {
                areaApply.setCreateTime(new Date().getTime() / 1000);
                int effectNum = areaApplyDao.insertAreaApply(areaApply);
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
    public List<AreaApply> getAreaApplyList(Map<String, Integer> paraMap) {
        return areaApplyDao.queryAreaApply(paraMap);
    }


    @Override
    public AreaApply getAreaApplyById(Integer applyId) {
        if (applyId != null) {
            String key = "areaApply:" + applyId;
            if (CacheUtils.isCached(key)) { //缓存了数据，直接拿出来
                return CacheUtils.getBean(key, AreaApply.class);
            } else {
                AreaApply areaApply = areaApplyDao.queryAreaApplyById(applyId);
                if (areaApply != null) {
                    areaApply.setUser(userService.queryUser(areaApply.getApplyUser()));
                    areaApply.setAreaName(func.areaIdToName(areaApply.getApplyArea()));
                    CacheUtils.saveBean(key, areaApply, Constants.WEEK_ONE);
                    return areaApply;
                } else {
                    return null;
                }
            }
        } else throw new RuntimeException("请求信息缺失");
    }

    @Override
    public Boolean changeStatus(AreaApply areaApply) {
        if (areaApply.getApplyId() != null) {
            int effectNum = areaApplyDao.updateApplyStatus(areaApply);
            if (effectNum > 0) { //销毁缓存
                String key = "areaApply:" + areaApply.getApplyId();
                if (CacheUtils.isCached(key)) {
                    CacheUtils.delKey(key);
                }
                return true;
            } else return false;
        } else throw new RuntimeException("请求参数出错");
    }

    @Override
    public Boolean userJoinApply(Relation relation) {
        try {
            int effectNum = applyUserDao.insertApplyUser(relation);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("报名活动失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("已报名");
        }

    }

    @Override
    public Boolean updateApplyUserStatus(List<Relation> relationList) {
        try {
            System.out.println("aaa");
            relationList.forEach(item -> {
                System.out.println(item.getUsername());
                int effectNum = applyUserDao.updateUserStatus(item);
                System.out.println(effectNum);
            });
            return true;
        } catch (Exception e) {
            throw new RuntimeException("编辑活动参与人名单失败");
        }
    }

    @Override
    public List<Relation> getApplyUserById(Integer applyId) {
        return applyUserDao.queryApplyUserByApplyId(applyId);
    }

    @Override
    public List<AreaApply> getApplyByUsername(String username) {
        return applyUserDao.queryApplyUserByUsername(username);
    }

    @Override
    public List<AreaApply> getMyAreaApply(String username) {
        return areaApplyDao.queryMyApplyArea(username);
    }

    @Override
    public Boolean deleteAreaApply(Integer applyId) {
        int effectNum = areaApplyDao.deleteAreaApply(applyId);
        if (effectNum > 0) { //删除成功，销毁缓存
            String key = "areaApply:" + applyId;
            if (CacheUtils.isCached(key)) {
                CacheUtils.delKey(key);
            }
            return true;
        } else {
            throw new RuntimeException("取消活动申请失败");
        }
    }

    @Override
    public Boolean deleteAreaApplyUser(Integer applyId, String username) {
        try {
            int effectNum = applyUserDao.deleteAreaApplyUser(username, applyId);
            if (effectNum > 0) {
                return true;
            } else {
                throw new StatusErrorException(210, "用户未报名该活动");
            }
        } catch (Exception e) {
            throw new StatusErrorException(210, "用户未报名");
        }

    }

    @Override
    public Boolean queryAreaApplyUserExist(Integer applyId, String username) {
        Relation relation = applyUserDao.queryAreaApplyUserExist(username, applyId);
        if (relation == null) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    public Boolean insertApplyComment(ApplyComment applyComment) {
        try {
            if (areaApplyDao.insertApplyComment(applyComment) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("已评论");
        }

    }

    @Override
    public List<ApplyComment> getApplyComment() {
        return areaApplyDao.queryApplyComment();
    }


}
