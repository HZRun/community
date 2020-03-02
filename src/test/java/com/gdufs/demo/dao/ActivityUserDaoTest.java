package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.AreaApply;
import com.gdufs.demo.entity.Relation;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityUserDaoTest {
    @Autowired
    private ActivityUserDao activityUserDao;

    @Test
    @Ignore
    public void insertApplyUser() {
        Relation relation = new Relation();
        relation.setApplyId(8);
        relation.setUsername("2016003457");
        int effectNum = activityUserDao.insertActivityUser(relation);
        assertEquals(effectNum, 1);
    }


    @Test
    @Ignore
    public void queryApplyUserByApplyId() {
        Integer applyId = 8;
        List<Relation> relation = activityUserDao.queryActivityUserByApplyId(applyId);
        assertEquals(relation.get(0).getUsername(), "2016003457");
    }

    @Test
    @Ignore
    public void queryApplyUserByUsername() {
        String username = "20161003457";
        List<ActivityApply> activtiyApplies = activityUserDao.queryActivityUserByUsername(username);
        assertEquals(activtiyApplies.get(0).getApplyArea().intValue(), 1001);
    }

    @Test
    public void deleteActivityUser() {
        String username = "2016003457";
        Integer applyId = 8;
        int effectNum = activityUserDao.deleteActivityUser(username, applyId);
        assertEquals(effectNum, 0);
    }

    @Test
    @Ignore
    public void queryActivityUserExistTest() {
        String username = "20161003456";
        Integer applyId = 20;
        Relation relation = activityUserDao.queryActivityUserExist(username, applyId);
        assertEquals(relation.getUsername(), username);
    }
}

