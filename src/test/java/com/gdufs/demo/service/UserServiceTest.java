package com.gdufs.demo.service;

import com.gdufs.demo.entity.User;
import com.gdufs.demo.entity.UserBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void queryUserService() {
        String userName = "20161003456";
        UserBase user = userService.queryUser(userName);
        assertEquals(user.getRealName(), "黄泽润");
    }
}
