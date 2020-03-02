package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.ActivityComment;
import com.gdufs.demo.entity.AreaApply;
import com.gdufs.demo.entity.AverageScoreEntity;
import com.gdufs.demo.utils.Func;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityApplyDaoTest {
    @Autowired
    private ActivityApplyDao activityApplyDao;

    @Test
    @Ignore
    public void insertActivityApplyTest() {
        ActivityApply activityApply = new ActivityApply();
        activityApply.setApplyUser("20161003457");
        activityApply.setSponsor("团委");
        //areaApply.setApplyId(10001);
        activityApply.setApplyArea(1001);
        activityApply.setTitle("活动申请dao测试");
        activityApply.setMembersLess(40);
        activityApply.setStartTime(Func.getTime(2019, 5, 3, 0, 30, 0));
        activityApply.setEndTime(Func.getTime(2019, 5, 3, 15, 20, 40));
        activityApply.setEnrollTime(Func.getTime(2019, 5, 3, 16, 20, 40));
        activityApply.setIntroduce("这是场地申请介绍，我在做dao测试");
        activityApply.setCreateTime(new Date().getTime() / 1000);
        activityApply.setThumbnail("");
        activityApply.setFullImage("");


        int effectNum = activityApplyDao.insertActivityApply(activityApply);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void queryActivityApply() {
        Map map = new HashMap<>();
        Date nowTime = Func.getTime(2019, 5, 10, 15, 9, 0);
        map.put("admin1Status", 1);
        map.put("admin2Status", 0);
        map.put("nowTime", nowTime);
        List<ActivityApply> activityApply = activityApplyDao.queryActivityApply(map);
        assertEquals(activityApply.get(0).getApplyUser(), "20161003456");
    }

    @Test
    @Ignore
    public void queryOfficeApply() {
        Integer admin2Status = 2;
        Date nowTime = Func.getTime(2019, 5, 17, 15, 9, 0);
        System.out.println(nowTime);
        List<ActivityApply> activityApplies = activityApplyDao.queryOfficeApply(admin2Status, new Date());
        assertEquals(activityApplies.get(0).getSponsor(), "广外学生处");
    }

    @Test
    @Ignore
    public void queryEnrollApply() {
        Date nowTime = new Date();
        List<ActivityApply> activityApplies = activityApplyDao.queryEnrollApply(nowTime);
        assertEquals(activityApplies.get(0).getSponsor(), "广外学生处");
    }

    @Test
    @Ignore
    public void queryMyApplyActivity() {
        String username = "20161003456";
        List<ActivityApply> activityApplies = activityApplyDao.queryMyApplyActivity(username);
        assertEquals(activityApplies.get(0).getSponsor(), "广外学生处");
    }

    @Test
    @Ignore
    public void queryActivityApplyById() {
        ActivityApply activityApply = activityApplyDao.queryActivityApplyById(1);
        assertEquals(activityApply.getMembersLess().intValue(), 50);
    }

    @Test
    @Ignore
    public void updateApplyStatus() {
        ActivityApply activityApply = new ActivityApply();
        activityApply.setActivityId(2);
        activityApply.setAdmin1Status(2);
        activityApply.setAdmin1Name("学生管理员1");
        activityApply.setAdmin1Advice("非法活动，不予批准");
        int effectNum = activityApplyDao.updateApplyStatus(activityApply);
        assertEquals(effectNum, 1);
    }

    @Test
    public void updateApplyStatusTest() {
        ActivityApply activityApply = new ActivityApply();
        activityApply.setActivityId(93);
        activityApply.setActivityStatus(1);
        activityApplyDao.updateApplyStatus(activityApply);
    }

    @Test
    @Ignore
    public void updateApplyStatus2() {
        ActivityApply activityApply = new ActivityApply();
        activityApply.setActivityId(2);
        activityApply.setActivityStatus(2);
        int effectNum = activityApplyDao.updateApplyStatus(activityApply);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void getFullImageById() {
        int activityId = 1;
        String fullImage = activityApplyDao.getFullImageById(activityId);
        assertEquals(fullImage, "");
    }

    @Test
    @Ignore
    public void insertActivityComment() {
        ActivityComment activityComment = new ActivityComment();
        activityComment.setActivityId(1002);
        activityComment.setContent("这是评价内容，，绝对好评");
        activityComment.setContentScore(3);
        activityComment.setOrganizeScore(4);
        activityComment.setUseScore(5);
        activityComment.setUsername("20161003456");
        activityComment.setCreateTime(new Date().getTime() / 1000);
        int effectNum = activityApplyDao.insertActivityComment(activityComment);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void queryActivityComment() {
        List<ActivityComment> activityComment = activityApplyDao.queryActivityComment();
        assertEquals(activityComment.get(0).getOrganizeScore().intValue(), 5);
    }

    @Test
    @Ignore
    public void deleteActivityApply() {
        Integer activityId = 5;
        int effectNum = activityApplyDao.deleteActivityApply(activityId);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void getAverageScoreTest() {
        Integer applyId = 12;
        AverageScoreEntity result = activityApplyDao.getActivityAverageScore(applyId);
        System.out.println(result.getOrganizeScore());
    }
}