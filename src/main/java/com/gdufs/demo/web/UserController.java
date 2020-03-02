package com.gdufs.demo.web;


import com.alibaba.fastjson.JSONObject;
import com.gdufs.demo.annotation.AdminAuthToken;
import com.gdufs.demo.annotation.AuthToken;
import com.gdufs.demo.config.FrameworkBaseConfig;
import com.gdufs.demo.dao.UserDao;
import com.gdufs.demo.entity.*;
import com.gdufs.demo.handler.InputErrorException;
import com.gdufs.demo.handler.PermissionException;
import com.gdufs.demo.service.UserService;
import com.gdufs.demo.utils.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private Msg msg;
    @Autowired
    private Func func;
    @Autowired
    private UserDao userDao;
    @Value("${app.host}")
    private String host_url;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Transactional
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String userName = jsonObject.getString("username").trim();
        String password = jsonObject.getString("password").trim();
        if (userName.equals("") || password.equals("")) {
            throw new InputErrorException(260, "请输入学号和密码");
        }
        String code = jsonObject.getString("code");
        //String realName = userService.login(userName, password);
        //String secretPwd = bCryptPasswordEncoder.encode(password);
        //System.out.println(secretPwd);
        Boolean flag = userService.checkPwd(userName, password);
        logger.info("flag:" + flag);

        if (flag) {//登录成功，可以获取到名字
            UserBase user = userService.queryUser(userName);
            if (user != null) { //查询结果不为空，已绑定手机号, 生成token并缓存
                if (user.getPhone().length() == 0) {
                    remap.put("msg", "未绑定手机");
                    remap.put("status", 208);
                } else {
                    String openId = WxFunc.getOpenId(code);
                    logger.info("code" + code);
                    logger.info("openId" + openId);
                    //if(openId!=null){
                    String oldOpenId = func.getOpenIdByUsername(userName);
                    if (oldOpenId.equals(openId)) {
                        //userService.updateOpenId(userName,openId);
                        //String key = "openid:"+userName;
                        //CacheUtils.delKey(key);
                        //}
                        Date date = new Date();
                        String tokenString = Func.createToken(userName, date);
                        CacheUtils.saveString("token:app:" + tokenString, userName, 60 * 60 * 24 * 30);
                        CacheUtils.saveString("username:" + userName, user.getRealName(), 60 * 60 * 24 * 30);
                        remap.put("token", tokenString);
                        remap.put("status", 200);
                        remap.put("msg", "success");
                    } else {
                        remap.put("msg", "账号与绑定微信号不匹配");
                        remap.put("status", 210);
                    }
                }
            } else {//第一次登录，强制绑定手机号
                remap.put("msg", "未绑定手机");
                remap.put("status", 208);
            }
        } else {
            remap.put("status", 209);
            remap.put("msg", "密码错误");
        }
        return remap;
    }

    @RequestMapping(value = "/open_login", method = RequestMethod.POST)
    public Map openLogin(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String userName = jsonObject.getString("username").trim();
        String password = jsonObject.getString("password").trim();
        Boolean flag = userService.checkPwd(userName, password);
        logger.info("flag:" + flag);
        if (flag) {
            remap.put("status", 200);
            remap.put("msg", "success");
            return remap;
        } else {
            remap.put("status", 209);
            remap.put("msg", "账号密码不匹配");
            return remap;
        }
    }

    @RequestMapping(value = "/admin_login", method = RequestMethod.POST)
    public Map<String, Object> adminLogin(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String userName = jsonObject.getString("username").trim();
        String password = jsonObject.getString("password").trim();
        Boolean flag = userService.checkPwd(userName, password);
        if (flag) {//用户账户密码对得上
            UserBase user = userService.queryUser(userName);
            if (user != null) {
                if (user.getAdminType().equals(0)) {
                    remap.put("status", 401);
                    remap.put("msg", "no admin");
                    return remap;
                } else {
                    Date date = new Date();
                    String tokenString = Func.createToken(userName, date);
                    CacheUtils.saveString("token:web:" + tokenString, userName, 60 * 60 * 24 * 3);
                    CacheUtils.saveString("username:" + userName, user.getRealName(), 60 * 60 * 24 * 30);
                    remap.put("token", tokenString);
                    remap.put("status", 200);
                    remap.put("msg", "success");
                    return remap;
                }

            } else {
                remap.put("status", 401);
                remap.put("msg", "no admin");
                return remap;
            }
        } else {
            remap.put("status", 209);
            remap.put("msg", "账号密码错误");
            return remap;
        }

    }

    @RequestMapping(value = "/login_out", method = RequestMethod.POST)
    public Map<String, Object> loginOut(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String userName = jsonObject.getString("username");
        String token = request.getHeader("Authorization");
        CacheUtils.delKey("token:" + token);
        CacheUtils.delKey("user:" + userName);
        remap.put("msg", "success");
        remap.put("status", 200);
        return remap;
    }


    @RequestMapping(value = "/create_code", method = RequestMethod.POST)
    public Map<String, Object> createCode(@RequestBody JSONObject jsonObject) {
        Map<String, Object> modelMap = new HashMap<>();
        String userName = jsonObject.getString("username").trim();
        String phone = jsonObject.getString("phone").trim();
        String code = Func.makeAuthCode();

        if (userService.queryPhoneExist(phone)) {//为true，手机号未绑定
            msg.sendMsg(phone, code);
            CacheUtils.saveString("code:" + phone, code, 180);
            modelMap.put("code", code);
            modelMap.put("status", 200);
            return modelMap;
        } else { //手机号已被绑定
            modelMap.put("msg", "手机号已注册");
            modelMap.put("status", 208);
            return modelMap;
        }

    }


    @Transactional
    @RequestMapping(value = "/bind_phone", method = RequestMethod.POST)
    public Map<String, Object> bindPhone(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String userName = jsonObject.getString("username").trim();
        String code = jsonObject.getString("code").trim();
        String wxCode = jsonObject.getString("wxCode"); //用于生成openId 的code.
        String phone = jsonObject.getString("phone").trim();
        String school = jsonObject.getString("school");//学院
        String realName = jsonObject.getString("realName").trim();//名字
        UserBase user = userService.queryUser(userName);
        String openId = WxFunc.getOpenId(wxCode);
        String oldUsername = userService.queryOpenIdExist(openId);
        if (oldUsername != null && !oldUsername.equals(userName)) { //已经存在该openId,拒绝绑定
            remap.put("msg", "该微信号已绑定其他账号");
            remap.put("status", 212);
            return remap;
        }
        if (user.getRealName().equals(realName) && user.getSchool().equals(school)) {
            if (CacheUtils.isCached("code:" + phone)) {
                String codeCache = CacheUtils.getString("code:" + phone);
                if (codeCache.equals(code)) {
                    Date date = new Date();
                    String tokenString = Func.createToken(userName, date);
                    CacheUtils.saveString("token:app:" + tokenString, userName, 60 * 60 * 24 * 30);
                    remap.put("token", tokenString);
                    userService.bindPhone(userName, phone);
                    logger.info("wxcode" + wxCode);
                    logger.info("openId" + openId);
                    if (openId != null) {
                        userService.updateOpenId(userName, openId);
                        String key = "openid:" + userName;
                        CacheUtils.delKey(key);
                    }
                    remap.put("msg", "绑定手机成功");
                    remap.put("status", 200);
                } else {
                    remap.put("msg", "验证码错误");
                    remap.put("status", 208);
                }
            } else {
                remap.put("msg", "验证码过期");
                remap.put("status", 208);
            }
        } else {
            remap.put("msg", "姓名学院学号信息不匹配");
            remap.put("status", 211);
        }
        return remap;
    }


    @AdminAuthToken
    @RequestMapping(value = "/set_admin", method = RequestMethod.POST)
    public Map<String, Object> setAdmin(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String username = jsonObject.getString("username");
        try {
            Integer userAdminType = userService.queryUser(username).getAdminType();
            if (userAdminType != 3) {
                throw new PermissionException(401, "没有权限");
            }
        } catch (Exception e) {
            throw new PermissionException(401, "没有权限");
        }

        String adminUser = jsonObject.getString("adminUser");
        String adminName = jsonObject.getString("adminName");
        String role = jsonObject.getString("role");
        String adminNote = jsonObject.getString("adminNote");
        if (adminName != null && adminUser != null && role != null) {
            UserBase user = userService.queryUser(adminUser);
            if (user != null && user.getRealName().equals(adminName)) {
                Integer adminType = 0;
                if (role.equals("学生")) {
                    adminType = 1;
                } else if (role.equals("教师")) {
                    adminType = 2;
                } else {
                    throw new InputErrorException(260, "角色只能输入学生或教师");
                }
                if (userService.setAdmin(adminUser, adminType, adminNote)) {
                    remap.put("status", 200);
                    remap.put("msg", "设置管理员成功");
                }
            } else {
                throw new RuntimeException("学号和姓名不对应");
            }
        } else throw new RuntimeException("请填写完整信息");
        return remap;
    }

    @AdminAuthToken
    @RequestMapping(value = "/del_admin", method = RequestMethod.POST)
    public Map<String, Object> delAdmin(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String username = jsonObject.getString("username");
        try {
            UserBase user = userService.queryUser(username);
            if (user == null) {
                throw new PermissionException(401, "没有权限");
            }

            Integer userAdminType = user.getAdminType();
            System.out.println(userAdminType);
            if (userAdminType != 3) {
                throw new PermissionException(401, "没有权限");
            }
        } catch (Exception e) {
            throw new InputErrorException(500, "未知错误");
        }

        String adminUser = jsonObject.getString("adminUser");
        //String adminName = jsonObject.getString("adminName");
        String adminNote = "";
        if (adminUser != null) {
            Integer adminType = 0;
            if (userService.setAdmin(adminUser, adminType, adminNote)) {
                remap.put("status", 200);
                remap.put("msg", "删除管理员成功");
            }

        } else throw new InputErrorException(260, "请填写完整信息");
        return remap;
    }

    @AuthToken
    @RequestMapping(value = "get_user_info", method = RequestMethod.POST)
    public ResultObject getUserInfo(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        UserBase user = userService.queryUser(username);
        return ResultObject.success(user);
    }

    @AuthToken
    @RequestMapping(value = "change_password", method = RequestMethod.POST)
    public ResultMap changePassword(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String newPwd = jsonObject.getString("newPwd");
        String oldPwd = jsonObject.getString("oldPwd");
        if (userService.updateUserPwd(username, newPwd, oldPwd)) {
            return ResultMap.success();
        } else {
            throw new InputErrorException(208, "原密码错误");
        }

    }

    @AdminAuthToken
    @RequestMapping(value = "get_admin_users")
    public ResultBean getAdminUsers(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        if (checksuperAdmin(username)) {
            List<User> users = userService.getAdminUsers();
            return ResultBean.success(users);
        } else throw new PermissionException(401, "没有权限");

    }

    private Boolean checksuperAdmin(String username) {
        try {
            UserBase user = userService.queryUser(username);
            if (user == null) {
                throw new PermissionException(401, "没有权限");
            }
            Integer userAdminType = user.getAdminType();
            if (userAdminType != 3) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new InputErrorException(500, "查询用户失败");
        }
    }

    @RequestMapping("get_username_by_token")
    public ResultObject getUsernameByToken(@RequestBody JSONObject jsonObject) {
        String username = func.getUsernameByToken(jsonObject.getString("token"));
        return ResultObject.success(username);
    }

    @RequestMapping("create_verify_code")
    public ResultObject createVerifyCode() {
        String verifyCode = Func.makeAuthCode();
        CacheUtils.saveString("verifyCode", verifyCode);
        return ResultObject.success(verifyCode);
    }

    @Transactional
    /*springboot的事务注解，加上这句话这个接口将支持事务，当然我
    在config/service/Transationma...下面进行了配置，(这种也算是框架规定的吧)
    */
    @RequestMapping("add_teacher")
    public ResultObject addTeacher(@RequestBody JSONObject jsonObject) {
        Map remap = new HashMap();
        String userName = jsonObject.getString("username").trim();
        String password = jsonObject.getString("password").trim();
        String verifyCode = jsonObject.getString("verifyCode").trim();
        String phone = jsonObject.getString("phone").trim();
        String phoneCode = jsonObject.getString("phoneCode").trim();
        String wxCode = jsonObject.getString("wxCode"); //用于生成openId 的code.
        String school = jsonObject.getString("school");//学院
        String realName = jsonObject.getString("realName").trim();//名字
        //jsonObject 是处理json格式很好用的一个包，getString方法获取了指定字段的值
        // 类似php的$realname = $jsonObject['realName']，
        if (userName.equals("") || password.equals("") || verifyCode.equals("") || phone.equals("") || phoneCode.equals("") || school.equals("") || realName.equals("")) {
            return ResultObject.error(260, "信息填写不完整");
        }
        String openId = WxFunc.getOpenId(wxCode);
        /*WxFunc是自己写的一个类(在util文件夹下面)，调用里面的方法getOpenId()
        在里面又进行一堆操作和调用能够返回用户的微信openId
        */
        String oldUsername = userService.queryOpenIdExist(openId);
        /*
        userService是自己写的用户服务类，提供诸如
        查询用户openid是否已经存在、根据用户名获得积分...等服务(方法)，
        下面我们进到这个函数里面
         */
        String storeVerifyCode = CacheUtils.getString("verifyCode");
        if (!verifyCode.equals(storeVerifyCode)) {
            return ResultObject.error(217, "授权码不正确");
        }
//        if(openId==null){
//            return ResultObject.error(216,"无法获得openID");
//        }
        if (oldUsername != null && !oldUsername.equals(userName)) { //已经存在该openId,拒绝绑定
            return ResultObject.error(212, "该微信号已绑定其他账号");
        }
        if (openId == null) {
            return ResultObject.error(218, "绑定微信号失败");
        }
        if (CacheUtils.isCached("code:" + phone)) {
            String codeCache = CacheUtils.getString("code:" + phone);
            if (codeCache.equals(phoneCode)) {
                User user = new User();
                user.setUserName(userName);
                user.setSchool(school);
                user.setRealName(realName);
                String secretPwd = bCryptPasswordEncoder.encode(password);
                user.setPwd(secretPwd);
                user.setPoints(10);
                user.setPhone(phone);
                user.setOpenId(openId);
                user.setCreateTime(Func.getIntTime());
                user.setAdminType(4);
                user.setGender("未知");
                Date date = new Date();
                String tokenString = Func.createToken(userName, date);
                CacheUtils.saveString("token:app:" + tokenString, userName, 60 * 60 * 24 * 30);
                userService.addUser(user);
                //userService.updateOpenId(userName,openId);
                //userService.bindPhone(userName, phone);
                logger.info("wxcode" + wxCode);
                logger.info("openId" + openId);
                return ResultObject.success(tokenString);
            } else {
                return ResultObject.error(208, "验证码错误");
            }
        } else {
            return ResultObject.error(208, "验证码不正确");
        }
    }

    @RequestMapping("add_user")
    public void addUserInfo() throws IOException {
        String defaultPwd = "123456";
        String secretPwd = bCryptPasswordEncoder.encode(defaultPwd);
        System.out.println(secretPwd);
        //HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream("G:\\javaweb\\学生基本信息upload.xls"));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream("/var/lib/tomcat8/webapps/ROOT/community/file/学生基本信息upload.xls"));
        //2.获取要解析的表格（第一个表格）
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        User user = new User();
        //获得最后一行的行号
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {//遍历每一行
            //3.获得要解析的行
            HSSFRow row = sheet.getRow(i);
            //4.获得每个单元格中的内容（String）
            String username = row.getCell(1).getStringCellValue();
            if (username.length() == 11) { //说明是学生学号
                if (userDao.queryUser(username) == null) { //用户未录入
                    String realName = row.getCell(0).getStringCellValue();
                    String gender = row.getCell(2).getStringCellValue();
                    String school = row.getCell(3).getStringCellValue();
                    user.setRealName(realName);
                    user.setUserName(username);
                    user.setGender(gender);
                    user.setSchool(school);
                    user.setPwd(secretPwd);
                    userService.addUser(user);
                }
            }
        }
        System.out.println("finish");
    }

    @RequestMapping("add_teacher_info")
    public void addTeacherInfo() throws IOException {
        String defaultPwd = "098765";
        String secretPwd = bCryptPasswordEncoder.encode(defaultPwd);
        System.out.println(secretPwd);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream("/var/lib/tomcat8/webapps/ROOT/community/file/教职工信息upload.xls"));
        //HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream("G:\\javaweb\\教职工信息upload.xls"));
        //2.获取要解析的表格（第一个表格）
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        User user = new User();
        //获得最后一行的行号
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {//遍历每一行
            //3.获得要解析的行
            HSSFRow row = sheet.getRow(i);
            //4.获得每个单元格中的内容（String）
            String username = row.getCell(0).getStringCellValue();
            if (username.length() == 9) { //说明是学生学号
                if (userDao.queryUser(username) == null) { //用户未录入
                    String realName = row.getCell(1).getStringCellValue();
                    String gender = "未知";
                    String school = row.getCell(2).getStringCellValue();
                    user.setRealName(realName);
                    user.setUserName(username);
                    user.setGender(gender);
                    user.setSchool(school);
                    user.setPwd(secretPwd);
                    user.setAdminType(4);
                    userService.addUser(user);
                }
            }
        }
        System.out.println("finish");
    }


}
