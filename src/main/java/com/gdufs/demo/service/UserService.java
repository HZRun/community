package com.gdufs.demo.service;

import com.gdufs.demo.entity.User;
import com.gdufs.demo.entity.UserBase;

import java.util.List;

public interface UserService {
    String login(String userName, String password);

    UserBase queryUser(String userName);

    Boolean checkPwd(String userName, String pwd);

    Boolean addUser(User user);

    Boolean queryPhoneExist(String phone);

    Boolean bindPhone(String userName, String phone); //绑定手机号

    Boolean updatePoints(String userName, Integer score);

    Boolean updateOpenId(String username, String openId);

    Boolean updateUserPwd(String username, String newPwd, String oldPwd);

    String getUserOpenId(String username);

    Boolean setAdmin(String userName, Integer adminType, String adminNote);

    List<User> getAdminUsers();

    String queryOpenIdExist(String openId);

}
