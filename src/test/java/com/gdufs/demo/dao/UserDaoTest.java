package com.gdufs.demo.dao;


import com.gdufs.demo.entity.User;
import com.gdufs.demo.entity.UserBase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @Test
    @Ignore
    public void insertUser() {
        User user = new User();
        user.setUserName("20161003456");
        user.setRealName("黄泽润");
        user.setSchool("信息学院");
        user.setGender("男");
        //user.setCreateTime(new Date().getTime()/1000);
        user.setPwd("aaa");
        int effectNum = userDao.insertUser(user);
        assertEquals(effectNum, 1);
    }

    @Test
    public void queryUser() {
        String userName = "20161003456";
        UserBase user = userDao.queryUser(userName);
        assertEquals(user.getRealName(), "黄泽润");
    }

    @Test
    public void queryUserAllInfo() {
        String userName = "20161003456";
        User user = userDao.queryUserAllInfo(userName);
        if (user != null) {
            System.out.println("aaaa");
        }
        assertEquals(user.getRealName(), "黄泽润");
    }

    @Test
    @Ignore
    public void queryPhoneExist() {
        String phone = "15521426086";
        String getPhone = userDao.queryPhoneExist(phone);
        assertEquals(getPhone, "15521426086");
    }

    @Test
    public void updateUserPhone() {
        String userName = "20161003456";
        String phone = "15521426086";
        int effectNum = userDao.updateUserPhone(userName, phone);
        assertEquals(effectNum, 1);
    }

    @Test
    public void updateAdminUser() {
        String userName = "20161003457";
        Integer adminType = 1;
        String adminNote = "";
        int effectNum = userDao.updateAdminUser(userName, adminType, adminNote);
        assertEquals(effectNum, 1);
    }

    @Test
    public void updatePoints() {
        String userName = "20161003456";
        Integer score = 5;
        int effectnum = userDao.updatePoints(userName, score);
        assertEquals(effectnum, 1);
    }

    @Test
    @Ignore
    public void getAdminUsers() {
        List<User> users = userDao.getAdminUsers();
        assertEquals(users.get(0).getUserName(), "20161003456");
    }

    @Test
    public void updateOpenIdTest() {
        String username = "20161003456";
        String openId = "this_is_my+open+id";
        int effectNum = userDao.updateOpenId(username, openId);
        assertEquals(effectNum, 1);
    }

    @Test
    public void getUserOpenIdTest() {
        String username = "20161003456";
        String openId = userDao.getUserOpenId(username);
        assertEquals(openId, "this_is_my+open+id");
    }

    @Test
    public void openIdExistTest() {
        String openId = "this_is_my+openid";
        String username = userDao.queryOpenId(openId);
        assertEquals(username, null);

    }
}
