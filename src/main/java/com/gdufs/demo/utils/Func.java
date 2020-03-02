package com.gdufs.demo.utils;

import com.gdufs.demo.entity.*;
import com.gdufs.demo.handler.PermissionException;
import com.gdufs.demo.service.ActivityApplyService;
import com.gdufs.demo.service.AreaApplyService;
import com.gdufs.demo.service.AreaService;
import com.gdufs.demo.service.UserService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 一些公用函数
 */
@Component
public class Func {
    @Autowired
    private UserService userService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private ActivityApplyService activityApplyService;
    @Autowired
    private AreaApplyService areaApplyService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 通过区域id获取区域名称，一般从缓存中读取
     *
     * @param areaId
     * @return String 区域名称
     */
    public String areaIdToName(Integer areaId) {
        String key = "area:" + areaId;
        if (CacheUtils.isCached(key)) {
            String value = CacheUtils.getString(key);
            return value;
        } else {
            Area area = areaService.getAreaById(areaId);
            if (area == null) {
                return null;
            } else {
                String areaName = area.getAreaName();
//                CacheUtils.saveString(key, areaName);
                redisTemplate.opsForValue().set(key, areaName);
                return areaName;
            }
        }
    }

    /**
     * 判断活动是否过了报名时间,参数为活动
     *
     * @param activityApply
     * @return
     */
    public Boolean isEnrollable(ActivityApply activityApply) {
        try {
            if (activityApply.getEnrollTime().compareTo(new Date()) > 0) { //报名时间>当前时间，可报名
                return true;
            } else { //不可报名
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("请求数据不存在");
        }
    }

    /**
     * 判断活动是否过了报名时间,参数为活动id
     *
     * @param activityId
     * @return
     */
    public Boolean isEnrollable(Integer activityId) {
        try {
            ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
            System.out.println(activityApply.getThumbnail());
            if (activityApply.getEnrollTime().compareTo(new Date()) > 0) { //报名时间>当前时间，可注册
                return true;
            } else { //不可报名
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("请求数据不存在");
        }
    }

    /**
     * 判断用户是否是管理员已经管理员类型
     * 0普通用户；1学生管理员；2老师管理员；3超级管理员
     *
     * @param username
     * @return
     */
    public Integer getAdminType(String username) {
        UserBase user = userService.queryUser(username);
        Integer type = user.getAdminType();
        return type;
    }

    /**
     * 通过用户名得到真实姓名，加入缓存减少数据库检索
     *
     * @param username
     * @return
     */
    public String usernameToName(String username) {
        String key = "username:" + username;
        if (CacheUtils.isCached(key)) {
            String value = CacheUtils.getString(key);
            return value;
        } else {
            UserBase user = userService.queryUser(username);
            if (user == null) {
                return null;
            } else {
                String realName = user.getRealName();
                redisTemplate.opsForValue().set(key, realName);
                return user.getRealName();
            }
        }
    }

    /**
     * 通过用户名得到真实姓名，加入缓存减少数据库检索
     *
     * @param username
     * @return
     */
    public String getOpenIdByUsername(String username) {
        String key = "openid:" + username;
        if (CacheUtils.isCached(key)) {
            System.out.println("openId有缓存");
            String value = CacheUtils.getString(key);
            return value;
        } else {
            System.out.println("openId没有缓存，从数据库拿");
            String openId = userService.getUserOpenId(username);
            if (openId == null) {
                return null;
            } else {
                redisTemplate.opsForValue().set(key, openId);
                return openId;
            }
        }
    }

    public static Boolean checkUserInBlacklist(String username) {
        if (CacheUtils.isCached("blacklist:" + username)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得活动状态
     * 1：未结束  2：已结束   3：未通过审核   4: 未审核   5:审核通过后用户取消(一般忽略之)
     *
     * @param activityApply
     * @return
     */
    public static Integer getActivityStatus(ActivityApply activityApply) {
        if (activityApply.getAdmin2Status() == 1) { //已通过审核，，判断是否已经结束
            if (activityApply.getEndTime().compareTo(new Date()) > 0) {// 还未结束
                return 1;
            } else { //已结束
                return 2;
            }
        } else if (activityApply.getAdmin2Status() == 2) { //未通过审核
            return 3;
        } else if (activityApply.getAdmin2Status() == 0) { //activityApply.getAdmin2Status()==0 未审核
            return 4;
        } else {  //admin2Status = 3 --> 用户通过后取消的活动(有别于场地申请，它所有活动取消都直接删除数据),一般不理结果为5的数据...
            return 5;
        }
    }

    /**
     * 获得场地申请状态
     * 1：未结束  2：已结束   3：未通过审核   4: 未审核
     *
     * @param areaApply
     * @return
     */
    public static Integer getAreaStatus(AreaApply areaApply) {
        if (areaApply.getAdmin2Status() == 1) { //已通过审核，，判断是否已经结束
            if (areaApply.getEndTime().compareTo(new Date()) > 0) {// 还未结束
                return 1;
            } else { //已结束
                return 2;
            }
        } else if (areaApply.getAdmin2Status() == 2) { //未通过审核
            return 3;
        } else { //activityApply.getAdmin2Status()==0 未审核
            return 4;
        }
    }


    /**
     * 手动构造时间，主要用户开发测试阶段
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @param second
     * @return
     */
    public static Date getTime(int year, int month, int day, int hour, int min, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        //月份从0开始，要减1
        calendar.set(Calendar.MONDAY, month - 1);
        calendar.set(Calendar.DATE, day);
        //不知道为什么，小时要减4....
        calendar.set(Calendar.HOUR, hour - 4);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, second);
        Date date = calendar.getTime();

        return date;
    }

    /**
     * 获得日期，格式20190511
     *
     * @return
     */
    public static String getDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONDAY) + 1);
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        return year + month + day;
    }

    /**
     * 获得n天以后的日期，格式：20190510
     *
     * @param dayAfter n天以后
     * @return 返回n天以后的日期
     */
    public static String getDate(int dayAfter) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, dayAfter);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONDAY) + 1);
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        return year + month + day;
    }

    /**
     * 将Date格式的某一天转换为20190523的格式
     *
     * @param date
     * @return
     */
    public static String getDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONDAY) + 1);
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        return year + month + day;
    }

    /**
     * 将时间转换为0000011100字符串索引
     *
     * @param date
     * @return
     */
    public static int timeToIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (hour == 0) {
            hour = 24;
        }
        int index = (hour - 7) * 2 + minute / 30;
        return index;
    }

    /**
     * 把指定位置的0替换为1，用于活动申请成功后，修改场地使用情况
     *
     * @param originString
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static String replaceZero(StringBuilder originString, int startIndex, int endIndex) {
        StringBuilder stringBuilder = originString;
        StringBuilder oneString = new StringBuilder();
        StringBuilder zeroBufferString = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            oneString.append(1);
            zeroBufferString.append(0);
        }
        String zeroString = new String(zeroBufferString); //后面和subString比较，需要都是String类型的
        int startPoint = startIndex;
        int endPoint = endIndex;

        String subString = stringBuilder.substring(startPoint, endPoint);
        if (!zeroString.equals(subString)) {
            return null;
        }
        String string1 = new String(oneString);
        stringBuilder.replace(startPoint, endPoint, string1);
        return new String(stringBuilder);
    }

    /**
     * 用于取消已发布活动(场地申请)时，更新usage,把1替换为0
     *
     * @param originString
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static String replaceOne(StringBuilder originString, int startIndex, int endIndex) {
        StringBuilder stringBuilder = originString;
        StringBuilder zeroString = new StringBuilder();
        StringBuilder oneBufferString = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            zeroString.append(0);
            oneBufferString.append(1);
        }
        String oneString = new String(oneBufferString); //后面和subString比较，需要都是String类型的
        int startPoint = startIndex;
        int endPoint = endIndex;

        String subString = stringBuilder.substring(startPoint, endPoint);
        if (!oneString.equals(subString)) {
            return null;
        }
        String string1 = new String(zeroString);
        stringBuilder.replace(startPoint, endPoint, string1);
        return new String(stringBuilder);
    }

    /**
     * 生成手机二维码
     *
     * @return
     */
    public static String makeAuthCode() {
        int authCodeNew = 0;
        authCodeNew = (int) Math.round(Math.random() * (9999 - 1000) + 1000);
        return Integer.toString(authCodeNew);
    }

    /**
     * 用户登录生成token
     *
     * @param username
     * @param date
     * @return
     */
    public static String createToken(String username, Date date) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .setExpiration(new Date(date.getTime() + 1000 * 60 * 24 * 30))
                .claim("username", username)
                .setIssuer("syk")
                .signWith(signatureAlgorithm, "Ionic");
        String jwt = builder.compact();
        return jwt;
    }

    /**
     * 为活动图片生成新名字
     *
     * @return
     */
    public static String createImageName() {
        Long fore = new Date().getTime();
        String tail = makeAuthCode();
        return tail + fore.toString();
    }

    /**
     * 活动当前时间时间戳
     *
     * @return
     */
    public static Long getIntTime() {
        return new Date().getTime() / 1000;
    }

    /**
     * 将指定的dateTime转换为时间戳
     *
     * @param date
     * @return
     */
    public static Long getIntTime(Date date) {
        return date.getTime() / 1000;
    }

    public static String timeFormatMinute(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String timeFormatOnlyMinute(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }


    /**
     * 用于生成pdf文件时，内容拼接
     *
     * @param document
     * @param applyId
     * @param type
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public Document addContent(Document document, Integer applyId, Integer type) throws IOException, DocumentException {
        if (type == 0) {//activity  本来以为还要导出场地申请，type用来判断是场地申请还是活动申请
            ActivityApply activityApply = activityApplyService.getActivityApplyById(applyId);
            String title = activityApply.getTitle();
            String content = activityApply.getIntroduce();
            String area = activityApply.getAreaName();
            Date date = activityApply.getStartTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateString = formatter.format(date);
            Integer joinNum = activityApply.getMembersLess();
            String sponsor = activityApply.getSponsor();
            Integer score = getAverageScore(applyId);

            //根据评分选取不同图片
            String imageName = "one_star.jpg";
            if (score == 1) {
                imageName = "one_star.jpg";
            } else if (score == 2) {
                imageName = "two_star.jpg";
            } else if (score == 3) {
                imageName = "three_star.jpg";
            } else if (score == 4) {
                imageName = "four_star.jpg";
            } else {
                imageName = "five_star.jpg";
            }
            String imagePath = Constants.PROJECTPATH + "image_file/" + imageName;
            Font font = new Font(BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
            try {
                document.add(new Paragraph("活动名称：" + title, font));
                document.add(new Paragraph("活动简介：", font));
                document.add(new Paragraph(content, font));
                document.add(new Paragraph("活动地点：" + area, font));
                document.add(new Paragraph("活动时间：" + dateString, font));
                document.add(new Paragraph("活动参与人数：" + joinNum, font));
                document.add(new Paragraph("举办人：" + sponsor, font));
                document.add(new Paragraph("活动评分：", font));
                //String imagePath = "/var/lib/tomcat8/webapps/ROOT/community/img/image_file/four_star.png";
                String winImagePath = "G:\\javaweb\\community\\src\\main\\resources\\static\\activity_thumbnail\\11231556892159739.jpg";
                System.out.println(imagePath);
                Image image = Image.getInstance(imagePath);
                image.scaleToFit(150, 40);
                document.add(image);
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        return document;
    }


    /**
     * 活动活动评分平均分
     *
     * @param activityId
     * @return
     */
    public Integer getAverageScore(Integer activityId) {
        AverageScoreEntity score = activityApplyService.getAverageScore(activityId);
        if (score == null) {
            return 5;
        } else {
            return (Math.round((score.getOrganizeScore() + score.getContentScore()) / 2));
        }
    }

    /**
     * 根据token获得用户名
     *
     * @param token
     * @return
     */
    public String getUsernameByToken(String token) {
        if (token != null && token.length() != 0) {
            if (CacheUtils.getString("token:web:" + token) != null) { //token 未过期
                return CacheUtils.getString("token:web:" + token);
            } else if (CacheUtils.getString("token:app:" + token) != null) {
                return CacheUtils.getString("token:app:" + token);
            } else {
                throw new PermissionException(210, "无效token");
            }
            //log.info("username is {}", username);
        } else {
            throw new PermissionException(210, "没有token传入");
        }
    }

}
