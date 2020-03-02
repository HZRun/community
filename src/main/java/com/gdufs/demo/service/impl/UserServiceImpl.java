package com.gdufs.demo.service.impl;

import com.gdufs.demo.dao.UserDao;
import com.gdufs.demo.entity.User;
import com.gdufs.demo.entity.UserBase;
import com.gdufs.demo.handler.InputErrorException;
import com.gdufs.demo.service.UserService;
import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.relation.RoleUnresolved;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密


    @Override
    public String login(String userName, String password) {
        //TODO 接学校登录接口
        if (true) {// 智慧广外验证登录成功,返回姓名
            return "李二鸣";
        } else {
            return null;
        }
//            User user = userDao.queryUser(userName);
//            if(user!=null){ //查询结果为空，说明不是第一次登录, 生成token并缓存
//                Date date = new Date();
//                String tokenString = Func.createToken(userName, date);
//                CacheUtils.hashSet("token", tokenString, userName);
//                CacheUtils.hashSet("realName", userName, user.getRealName());
//            }else {//第一次登录，强制绑定手机号
//
//            }
    }

    @Override
    public UserBase queryUser(String userName) {
        UserBase userBase = userDao.queryUser(userName);
        if (userBase != null) {
            return userBase;
        } else throw new RuntimeException("用户不存在");
    }

    @Override
    public Boolean checkPwd(String userName, String pwd) {
        //User user = userDao.queryUserAllInfo(userName);
        //System.out.println(user.getRealName());
        User user = userDao.queryUserAllInfo(userName);
        if (user == null) {
            throw new InputErrorException(208, "账户不存在");
        }
        if (!bCryptPasswordEncoder.matches(pwd, user.getPwd())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean addUser(User user) {
        try {
            int effectNum = userDao.insertUser(user);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("创建用户失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("创建用户失败");
        }

    }

    @Override
    public Boolean queryPhoneExist(String phone) {
        try {
            String phoneNum = userDao.queryPhoneExist(phone);
            if (phoneNum != null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException("出现未知错误");
        }

    }

    @Override
    public Boolean bindPhone(String userName, String phone) {
        try {
            int effectNum = userDao.updateUserPhone(userName, phone);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("绑定手机号失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("绑定手机号失败");
        }
    }

    @Override
    public Boolean updatePoints(String userName, Integer score) {
        try {
            int effectNum = userDao.updatePoints(userName, score);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("更新积分出错");
            }
        } catch (Exception e) {
            throw new RuntimeException("更新积分出错");
        }
    }

    @Override
    public Boolean updateOpenId(String username, String openId) {
        int effectNum = userDao.updateOpenId(username, openId);
        if (effectNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean updateUserPwd(String username, String newPwd, String oldPwd) {
        User user = userDao.queryUserAllInfo(username);
        if (!bCryptPasswordEncoder.matches(oldPwd, user.getPwd())) {
            return false;
        } else {
            String secretPwd = bCryptPasswordEncoder.encode(newPwd);
            int effectNum = userDao.updateUserPwd(username, secretPwd);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("更新密码失败");
            }

        }

    }

    @Override
    public String getUserOpenId(String username) {
//        User user = userDao.queryUser(username);
//        if(user==null){
//            throw new RuntimeException("该用户不存在");
//        }else {
////            String openId
//        }
//        return null;
        String openId = userDao.getUserOpenId(username);
        return openId;
    }

    @Override
    public Boolean setAdmin(String userName, Integer adminType, String adminNote) {
        try {
            int effectNum = userDao.updateAdminUser(userName, adminType, adminNote);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("改变用户权限出错");
            }
        } catch (Exception e) {
            throw new RuntimeException("改变用户权限出错");
        }
    }

    @Override
    public List<User> getAdminUsers() {
        return userDao.getAdminUsers();
    }

    @Override
    public String queryOpenIdExist(String openId) {
        String username = userDao.queryOpenId(openId);
        //这个函数只有两句话，因为它调用了userDao这个类里面的方法
        //userDao是用来提供和用户相关数据库操作的方法
        return username;
    }
}
