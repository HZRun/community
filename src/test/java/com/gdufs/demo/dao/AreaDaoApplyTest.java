package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ApplyComment;
import com.gdufs.demo.entity.Area;
import com.gdufs.demo.entity.AreaApply;
import com.gdufs.demo.utils.Func;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaDaoApplyTest {
    @Autowired
    private AreaApplyDao areaApplyDao;

    @Test
    @Ignore
    public void insertAreaApply() {
        AreaApply areaApply = new AreaApply();
        areaApply.setApplyUser("20161003456");
        areaApply.setSponsor("学生处");
        //areaApply.setApplyId(10001);
        areaApply.setApplyArea(1001);
        areaApply.setStartTime(Func.getTime(2019, 5, 2, 0, 30, 0));
        areaApply.setEndTime(Func.getTime(2019, 5, 2, 16, 20, 40));
        areaApply.setIntroduce("这是场地申请介绍，我在做dao测试");
        areaApply.setCreateTime(new Date().getTime() / 1000);
        int effectNum = areaApplyDao.insertAreaApply(areaApply);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void queryAreaApply() {
        Map<String, Integer> map = new HashMap<>();
        // 根据admin1_status和admin2_status的不同值，得到不同结果
        // 1. 全为null,获取全部数据
        // 2. admin1_status=1 获得预审核通过的数据
        // 3. admin2_status=1 获得审核通过的数据
        // 4. admin1_status=0 获得未审核通过updateApplyStatus的数据
        // 5. admin1_status=2 获得预审核 未审核通过的数据
        //map.put("admin1status", 1);

        map.put("admin2Status", 0);
        if (map.get("admin2Status") != null) {
            System.out.println("非空");
        }
        List<AreaApply> areaApply = areaApplyDao.queryAreaApply(map);
        assertEquals(areaApply.get(0).getSponsor(), "生处");
    }

    @Test
    public void queryMyApplyArea() {
        String username = "20161003456";
        List<AreaApply> areaApplies = areaApplyDao.queryMyApplyArea(username);
        assertEquals(areaApplies.get(0).getSponsor(), "黄泽润");
    }

    @Test
    @Ignore
    public void queryAreaApplyById() {
        Integer applyId = 4;
        AreaApply areaApply = areaApplyDao.queryAreaApplyById(applyId);
        assertEquals(areaApply.getApplyUser(), "20161003456");
    }

    @Test
    @Ignore
    public void updateApplyStatus() {
        AreaApply areaApply = new AreaApply();
        areaApply.setApplyId(3);
        areaApply.setAdmin1Status(2);
        areaApply.setAdmin1Advice("非法活动，不予批准");
        int effectNum = areaApplyDao.updateApplyStatus(areaApply);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void insertApplyComment() {
        ApplyComment applyComment = new ApplyComment();
        applyComment.setApplyId(116);
        applyComment.setContent("这是评价内容，，绝对好评");
        applyComment.setContentScore(3);
        applyComment.setOrganizeScore(4);
        applyComment.setUseScore(5);
        applyComment.setUsername("20161003456");
        applyComment.setCreateTime(new Date().getTime() / 1000);
        int effectNum = areaApplyDao.insertApplyComment(applyComment);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void queryApplyComment() {
        List<ApplyComment> applyComment = areaApplyDao.queryApplyComment();
        assertEquals(applyComment.get(0).getOrganizeScore().intValue(), 5);
    }


}
