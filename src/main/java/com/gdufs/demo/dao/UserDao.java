package com.gdufs.demo.dao;

import com.gdufs.demo.entity.User;
import com.gdufs.demo.entity.UserBase;

import java.util.List;

public interface UserDao {
    int insertUser(User user);

    UserBase queryUser(String userName);

    User queryUserAllInfo(String userName);

    String queryPhoneExist(String phone);

    int updateOpenId(String username, String openId);

    String queryOpenId(String openId);
    //这个函数的接口定义在这儿，接口的实现在mapper的UserDao.xml中

    String getUserOpenId(String username);

    List<User> getAdminUsers();

    int updateUserPhone(String userName, String phone);

    int updateUserPwd(String username, String newPwd);

    int updateAdminUser(String userName, Integer adminType, String adminNote);

    int updatePoints(String userName, Integer score);

}
