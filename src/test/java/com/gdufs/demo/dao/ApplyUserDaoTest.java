package com.gdufs.demo.dao;

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
public class ApplyUserDaoTest {
    @Autowired
    private ApplyUserDao applyUserDao;

    @Test
    @Ignore
    public void insertApplyUser() {
        Relation relation = new Relation();
        relation.setApplyId(8);
        relation.setUsername("2016003456");
        int effectNum = applyUserDao.insertApplyUser(relation);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void updateApplyUserStatus() {
        Relation relation = new Relation();
        relation.setApplyId(125);
        relation.setUsername("20161003456");
        relation.setStatus(0);
        int effectNum = applyUserDao.updateUserStatus(relation);
        assertEquals(effectNum, 4);
    }

    @Test
    @Ignore
    public void queryApplyUserByApplyId() {
        Integer applyId = 9;
        List<Relation> relation = applyUserDao.queryApplyUserByApplyId(applyId);
        assertEquals(relation.get(0).getUsername(), "20161003456");
    }

    @Test
    @Ignore
    public void queryApplyUserByUsername() {
        String username = "20161003456";
        List<AreaApply> areaApplies = applyUserDao.queryApplyUserByUsername(username);
        assertEquals(areaApplies.get(0).getSponsor(), "黄泽润");
    }

    @Test
    @Ignore
    public void deleteAreaApplyUser() {
        String username = "20161003458";
        Integer applyId = 8;
        int effectNum = applyUserDao.deleteAreaApplyUser(username, applyId);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void queryAreaApplyExistTest() {
        String username = "20161003456";
        Integer applyId = 70;
        Relation relation = applyUserDao.queryAreaApplyUserExist(username, applyId);
        assertEquals(relation.getUsername(), username);
    }
}
