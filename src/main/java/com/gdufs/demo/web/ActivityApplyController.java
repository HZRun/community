package com.gdufs.demo.web;

import com.alibaba.fastjson.JSONObject;
import com.gdufs.demo.annotation.AdminAuthToken;
import com.gdufs.demo.annotation.AuthToken;
import com.gdufs.demo.entity.*;
import com.gdufs.demo.handler.InputErrorException;
import com.gdufs.demo.handler.PermissionException;
import com.gdufs.demo.handler.StatusErrorException;
import com.gdufs.demo.service.*;
import com.gdufs.demo.utils.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/activity")
public class ActivityApplyController {

    private Logger logger = LoggerFactory.getLogger(ActivityApplyController.class);
    @Autowired
    private ActivityApplyService activityApplyService;
    @Autowired
    private UserService userService;
    @Autowired
    private Func func;
    @Autowired
    private AreaApplyService areaApplyService;
    @Autowired
    private ActivityComplaintService activityComplaintService;
    @Autowired
    private ActivityPunishmentService activityPunishmentService;
    @Autowired
    private WxFunc wxFunc;
    @Autowired
    private UsageRecordService usageRecordService;

    //活动申请接口
    @AuthToken
    @Transactional
    @RequestMapping(value = "apply_activity", method = RequestMethod.POST)
    public Map<String, Object> applyActivity(@RequestBody JSONObject jsonObject) {
        Map<String, Object> modelMap = new HashMap<>();
        ActivityApply activityApply = new ActivityApply();
        String userName = jsonObject.getString("username");
        Integer areaId = jsonObject.getInteger("areaId");
        Date enrollTime = jsonObject.getDate("enrollTime");
        Date startTime = jsonObject.getDate("startTime");
        Date endTime = jsonObject.getDate("endTime");
        String introduce = jsonObject.getString("introduce");
        String formId = jsonObject.getString("formId");
        String title = jsonObject.getString("title");
        String imageFile = jsonObject.getString("imageFile");
        Integer memberLess = jsonObject.getInteger("memberLess");
        if (areaId == null || enrollTime == null || startTime == null || endTime == null || memberLess == null || title == null || introduce == null) {
            throw new InputErrorException(260, "输入信息不完整");
        }
        if (formId != null) {
            wxFunc.saveFormId(userName, formId);
        }
        if (Func.checkUserInBlacklist(userName)) { //在黑名单中，不允许发帖
            throw new PermissionException(401, "黑名单中用户不允许申请活动");
        }
        Integer adminType = func.getAdminType(userName);
        activityApply.setApplyArea(areaId);
        activityApply.setApplyUser(userName);
        activityApply.setTitle(title);
        activityApply.setMembersLess(memberLess);
        activityApply.setStartTime(startTime);
        activityApply.setEndTime(jsonObject.getDate("endTime"));
        if (startTime.compareTo(new Date()) < 0) { //开始时间早于当下时间，不允许
            throw new InputErrorException(240, "开始时间不应早于当下时间");
        } else if (startTime.compareTo(enrollTime) < 0) {//开始时间早于报名时间
            throw new InputErrorException(240, "报名截止时间不能晚于开始时间");
        } else if (enrollTime.compareTo(new Date()) < 0) {
            throw new InputErrorException(240, "报名截止开始时间不应早于当下时间");
        }
        activityApply.setEnrollTime(enrollTime); //报名截止时间
        activityApply.setIntroduce(introduce);

        //判断是否和场地可用时间冲突
        int startTimeIndex = Func.timeToIndex(startTime);
        int endTimeIndex = Func.timeToIndex(endTime);
        String day = Func.getDate(startTime);
        String key = "usage:" + day + ":" + areaId;
        String originUsage = CacheUtils.getString(key);
        System.out.println(originUsage);
        String newUsage = Func.replaceZero(new StringBuilder(originUsage), startTimeIndex, endTimeIndex);
        if (newUsage == null) {  //冲突了
            modelMap.put("status", 230);
            modelMap.put("msg", "所选时间场地不空闲");
            return modelMap;
        }
        String thumbnaill = "";
        String fullImage = "";
        if (imageFile != null && imageFile.length() > 0) {//上传了图片
            String basePath = Constants.BASEPATH;
            String image = imageFile;
            image = image.substring(image.indexOf(",") + 1);//attention！逗号前的字符不用decode,哭晕在厕所
            //image=image.replaceAll(" ", "+");
            //image=image.replaceAll("data:image/png;base64,","");
            String imageName = Func.createImageName();
            String thumbnailFile = "activity_thumbnail/" + imageName + ".jpg";
            String fullImg = "activity_image/" + imageName + ".jpg";
            String thumbFilePath = basePath + thumbnailFile;
            System.out.println(basePath + fullImg);
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                byte[] b = decoder.decodeBuffer(image);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {//调整异常数据
                        b[i] += 256;
                    }
                }

                String fullImagePath = basePath + fullImg;
                OutputStream out = new FileOutputStream(fullImagePath);
                out.write(b);
                out.flush();
                out.close();
                Thumbnails.of(fullImagePath).size(1000, 500).toFile(thumbFilePath);
                thumbnaill = thumbnailFile;
                fullImage = fullImg;
            } catch (Exception e) {
                throw new RuntimeException("图片上传失败");
            }
        }

        activityApply.setFullImage(fullImage);
        activityApply.setThumbnail(thumbnaill);
        //applyUser根据token得到
        //activityApply.setApplyUser(jsonObject.getString("applyUser"));
        if (jsonObject.getString("sponsor") != null) {
            activityApply.setSponsor(jsonObject.getString("sponsor"));
        } else {
            if (CacheUtils.isCached("username:" + userName)) {//用户名，真实姓名被缓存，直接拿
                activityApply.setSponsor(CacheUtils.getString("username:" + userName));
            } else {
                String sponsor = func.usernameToName(userName);
                //String sponsor = "广外";//userService.queryUser(userName).getRealName();
                activityApply.setSponsor(sponsor);
            }
        }
        if (adminType == 1) {
            activityApply.setAdmin1Status(1);
            activityApply.setAdmin1Name(func.usernameToName(userName));
        } else if (adminType == 2 || adminType == 3) { //老师管理员发布的活动，直接通过，修改usage
            activityApply.setAdmin2Status(1);
            activityApply.setAdmin2Name(func.usernameToName(userName));
        }
        if (activityApplyService.applyActivity(activityApply)) {
            //管理员自己生成的活动默认就不生成惩罚了createActivityPunishment();
            modelMap.put("status", 200);
            modelMap.put("msg", "success");
            System.out.println(activityApply.getActivityId());
            if (adminType == 2 || adminType == 3) {
                UsageRecord usageRecord = new UsageRecord();
                usageRecord.setAdminUser(userName);
                usageRecord.setApplyId(activityApply.getActivityId());
                usageRecord.setAreaId(areaId);
                usageRecord.setDay(day);
                usageRecord.setStartTimeIndex(startTimeIndex);
                usageRecord.setEndTimeIndex(endTimeIndex);
                usageRecord.setType(0);
                usageRecordService.insertUsageRecord(usageRecord);
                String key2 = "area_use:" + areaId + ":" + day + ":activity";
                //String key2 = "area_use:"+areaId+":"+day;
                CacheUtils.saveToSet(key2, activityApply.getActivityId().toString());
                CacheUtils.saveString(key, newUsage);
            }

        } else {
            modelMap.put("status", 500);
            modelMap.put("msg", "fail");
        }
        return modelMap;
    }

    //用于后台管理 获取活动
    // 根据admin1_status和admin2_status的不同值，得到不同结果
    // 1. 全为null,获取全部数据
    // 2. admin1Status=1 admin2Status=0 获得预审核通过的数据，等待终审
    // 3. admin2Status=1 获得审核通过的数据
    // 4. admin1Status=0 获得未审核的数据
    // 5. admin1Status=2 获得预审核 不通过的数据
    // 6. admin1Status=1,admin2Status=2 获得预审核通过，终审不通过的数据(不存在了)
    // 7. admin2Status=2 获得终审不通过的数据，可能未经过初审
    @AuthToken
    @RequestMapping(value = "get_activities", method = RequestMethod.POST)
    public Map<String, Object> getActivities(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        Map paraMap = new HashMap<>();
        String type = jsonObject.getString("type");
        if (type.equals("prePass")) { // 2
            paraMap.put("admin1Status", 1);
            paraMap.put("admin2Status", 0);
        } else if (type.equals("pass")) { //3
            paraMap.put("admin2Status", 1);
        } else if (type.equals("noCheck")) { //4
            paraMap.put("admin1Status", 0);
            paraMap.put("admin2Status", 0);
        } else if (type.equals("preNoPass")) { //5
            paraMap.put("admin1Status", 2);
        }
//        else if(type.equals("passNoPass")){ //6
//            paraMap.put("admin1Status", 1);
//            paraMap.put("admin2Status", 2);
//        }
        else if (type.equals("noPass")) {
            paraMap.put("admin2Status", 2);
        } else {
            throw new InputErrorException(500, "没有该活动类型");
        }

        paraMap.put("nowTime", new Date());
        List<ActivityApply> activityApplyList = activityApplyService.getActivityApplyList(paraMap);
        activityApplyList.forEach(item -> {
            String name = func.areaIdToName(item.getApplyArea());
            if (item.getThumbnail().length() > 0) {
                item.setThumbnail(Constants.PHOTOROOT + item.getThumbnail());
            } else {
                item.setThumbnail(Constants.DEFAULT_IMG_PATH);
            }
            item.setUser(userService.queryUser(item.getApplyUser()));
            item.setAreaName(name);
        });
        remap.put("activityApplyList", activityApplyList);
        remap.put("status", 200);
        return remap;
    }

    //通过活动id，获取活动
    @Transactional
    @AuthToken
    @RequestMapping(value = "get_activity_by_id")
    public Map<String, Object> queryActivityById(@RequestBody JSONObject jsonObject) throws InterruptedException {
        Map<String, Object> remap = new HashMap<>();
        Map<String, Integer> param = new HashMap<>();
        Integer activityId = jsonObject.getInteger("activityId");
        String username = jsonObject.getString("username");
        String formId = jsonObject.getString("formId");
        if (formId != null) {
            wxFunc.saveFormId(username, formId);
        }
        Integer isJoin = 0;
        ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);

        String areaName = func.areaIdToName(activityApply.getApplyArea());
        if (activityApplyService.queryActivityUserExist(activityId, username)) {
            isJoin = 1;
        }
        activityApply.setAreaName(areaName);
        List<Relation> relations = activityApplyService.getActivityUserById(activityId);//获得报名人数
        Integer joinNum = relations.size();//报名人数
        activityApply.setAreaName(func.areaIdToName(activityApply.getApplyArea()));
        // <逻辑改了....直接判断activityStatus来确定可否报名。>
        // activityStatus 表示活动状态：未审核4，未通过审核3，已结束2，未结束1(注意和数据库字段activityStatus产生歧义)
        //数据库中存放的activityStatus用于保存活动是否截止报名了0否，1已截止
        //每次调用本接口时，判断enrollTime 是否小于当前时间，
        //若为是：活动开放报名；若为否：获取activityStatus的值：
        //   0->看是否满足最低人数要求：是的话将值置为1，向已报名用户发送消息，否的话将值置为2
        //   1 / 2 -> 报名时间已到；

//        if(func.isEnrollable(activityApply)){  //未到报名截止时间
//           remap.put("isEnrollable",1);
//        }else {
//            remap.put("isEnrollable",0);
//            Integer status = activityApply.getActivityStatus();
//            if(status==0){ //状态需要改变了,并且调用发送消息函数
//                wxFunc.sendActivityUserMsg(activityId);
//            }
//        }
        if (activityApply.getActivityStatus() == 0) {  //这里的ActivityStatus是数据库保存的活动状态，0可报名，1报名结束，，和下面的ActvityStatus意义不太（偷懒没多写一个变量，别打我）
            remap.put("isEnrollable", 1);
        } else {
            remap.put("isEnrollable", 0);
        }
        activityApply.setActivityStatus(Func.getActivityStatus(activityApply));
        remap.put("activityApply", activityApply);
        remap.put("joinNum", joinNum);
        remap.put("isJoin", isJoin);
        remap.put("status", 200);
        remap.put("msg", "success");
        return remap;
    }


    //管理人员审批活动
    @Transactional
    @AdminAuthToken
    @RequestMapping(value = "change_status", method = RequestMethod.POST)
    public ResultBean changeStatus(@RequestBody JSONObject jsonObject) {
        Integer adminStatus = jsonObject.getInteger("adminStatus"); //1通过，2不通过
        String adminAdvice = jsonObject.getString("adminAdvice");
        Integer activityId = jsonObject.getInteger("activityId");
        String username = jsonObject.getString("username");
        String realName = func.usernameToName(username);
        ActivityApply applyEntity = new ActivityApply();
        applyEntity.setActivityId(activityId);
        Integer adminType = func.getAdminType(username);
        ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
        String applyUser = activityApply.getApplyUser();
        Date startTime = activityApply.getStartTime();
        Date endTime = activityApply.getEndTime();
        Integer areaId = activityApply.getApplyArea();
        if (adminType == 0) {
            throw new PermissionException(401, "普通用户，权限不足");
        } else if (adminType == 1) { //学生管理员
            if (adminStatus == 1) { //欲判断为通过，看看场地是否空闲
                //判断是否和场地可用时间冲突
                int startTimeIndex = Func.timeToIndex(startTime);
                int endTimeIndex = Func.timeToIndex(endTime);
                String day = Func.getDate(startTime);
                String key = "usage:" + day + ":" + areaId;
                String originUsage = CacheUtils.getString(key);
                System.out.println(originUsage);
                String newUsage = Func.replaceZero(new StringBuilder(originUsage), startTimeIndex, endTimeIndex);
                if (newUsage == null) {  //冲突了
                    throw new StatusErrorException(230, "场地可用时间冲突");
                }
            }
            applyEntity.setAdmin1Status(adminStatus);
            applyEntity.setAdmin1Advice(adminAdvice);
            applyEntity.setAdmin1Name(realName);
            if (activityApplyService.changeStatus(applyEntity)) {
                if (adminStatus == 2) { //审批不通过，发送不通过消息
                    String formId = wxFunc.getFormId(applyUser);
                    String openId = func.getOpenIdByUsername(applyUser);
                    if (formId != null && openId != null) {
                        wxFunc.pushActivityApplyFail(formId, openId, activityApply.getTitle(), adminAdvice);
                    }
                    return ResultBean.success();
                    //sendAreaFail();
                }
            } else throw new RuntimeException("审批失败");
        } else {  //教师管理员
            applyEntity.setAdmin2Advice(adminAdvice);
            applyEntity.setAdmin2Name(realName);
            applyEntity.setAdmin2Status(adminStatus);

            int startTimeIndex = Func.timeToIndex(startTime);
            int endTimeIndex = Func.timeToIndex(endTime);
            String day = Func.getDate(startTime);


            if (adminStatus == 2) { //审批不通过，发送不通过消息
                String formId = wxFunc.getFormId(applyUser);
                String openId = func.getOpenIdByUsername(applyUser);
                if (formId != null && openId != null) {
                    wxFunc.pushActivityApplyFail(openId, formId, activityApply.getTitle(), adminAdvice);
                }
                // sendAreaFail()
                activityApplyService.changeStatus(applyEntity);
                return ResultBean.success();
            } else if (adminStatus == 1) { //审批通过，先判断可用时间是否冲突；
                //检测场地是否空闲
                String key = "usage:" + day + ":" + areaId;
                System.out.println(key);
                if (!CacheUtils.isCached(key)) {
                    throw new RuntimeException("场地情况丢失");
                }
                String originUsage = CacheUtils.getString(key);
                String newUsage = Func.replaceZero(new StringBuilder(originUsage), startTimeIndex, endTimeIndex);
                System.out.println(newUsage);
                if (newUsage != null) { //发送通过消息，修改场地可用时间
                    activityApplyService.changeStatus(applyEntity);
                    createActivityPunishment(activityId);
                    String openId = func.getOpenIdByUsername(applyUser);
                    logger.info("openId:" + openId);
                    String formId = wxFunc.getFormId(applyUser);
                    logger.info("formId:" + formId);
                    UsageRecord usageRecord = new UsageRecord();
                    usageRecord.setAdminUser(username);
                    usageRecord.setApplyId(activityId);
                    usageRecord.setAreaId(areaId);
                    usageRecord.setDay(day);
                    usageRecord.setStartTimeIndex(startTimeIndex);
                    usageRecord.setEndTimeIndex(endTimeIndex);
                    usageRecord.setType(0);
                    usageRecordService.insertUsageRecord(usageRecord);
                    if (openId != null && formId != null) {
                        logger.info("openId:" + openId);
                        logger.info("formId:" + formId);
                        wxFunc.pushActivityApplySuccess(openId, formId, activityApply.getTitle());
                    }
                    CacheUtils.saveString(key, newUsage);
                    String key2 = "area_use:" + areaId + ":" + day + ":activity";
                    //String key2 = "area_use:"+areaId+":"+day;
                    CacheUtils.saveToSet(key2, activityId.toString());
                    return ResultBean.success();
                    //TO sendAreaSuccess()
                } else {
                    throw new StatusErrorException(230, "场地可用时间冲突");
                }
            }

        }
        return ResultBean.success();
    }


    //获取学生处 发布/审核的活动
    @AdminAuthToken
    @RequestMapping(value = "get_office_activities", method = RequestMethod.POST)
    public Map<String, Object> getOfficeActivities(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        String type = jsonObject.getString("type");//待审核0,已发布1
        Integer admin2Status = 0;
        if (type.equals("已发布")) {
            admin2Status = 1;
        } else if (type.equals("待审核")) {
            admin2Status = 0;
        } else {
            throw new InputErrorException(260, "只能选择'已发布'和'待审核'两种类型");
        }
        List<ActivityApply> activityApplyList = activityApplyService.queryOfficeApply(admin2Status);
        activityApplyList.forEach(item -> {
            String name = func.areaIdToName(item.getApplyArea());
            item.setAreaName(name);
            item.setUser(userService.queryUser(item.getApplyUser()));
            if (item.getThumbnail().length() > 0) {
                item.setThumbnail(Constants.PHOTOROOT + item.getThumbnail());
            } else {
                item.setThumbnail(Constants.DEFAULT_IMG_PATH);
            }
        });
        remap.put("activityApplyList", activityApplyList);
        remap.put("status", 200);
        return remap;
    }


    //获取活动图片原图
    @AuthToken
    @RequestMapping(value = "get_image", method = RequestMethod.POST)
    public Map<String, Object> getImage(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        Integer activityId = jsonObject.getInteger("activityId");
        String fullImage = activityApplyService.getActivityImage(activityId);
        remap.put("fullImage", fullImage);
        remap.put("status", 200);
        return remap;
    }

    //管理员获取活动评价
    @AdminAuthToken
    @RequestMapping(value = "get_activity_comments", method = RequestMethod.POST)
    public ResultObject getActivityComments() {
        List<ActivityComment> activityComments = activityApplyService.getActivityComment();
        activityComments.forEach(item -> {
            item.setActivityApply(activityApplyService.getActivityApplyById(item.getActivityId()));
        });
        return ResultObject.success(activityComments);
    }

    //活动评价
    @RequestMapping(value = "post_activity_comment", method = RequestMethod.POST)
    public Map<String, Object> postActivityComment(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        Integer activityId = jsonObject.getInteger("activityId");
        String usename = jsonObject.getString("username");
        Integer contentScore = jsonObject.getInteger("contentScore");
        Integer organizeScore = jsonObject.getInteger("organizeScore");
        Integer useScore = jsonObject.getInteger("useScore");
        String content = jsonObject.getString("content");
        Long createTime = new Date().getTime() / 1000;

        ActivityComment activityComment = new ActivityComment();
        activityComment.setUsername(usename);
        activityComment.setUseScore(useScore);
        activityComment.setCreateTime(createTime);
        activityComment.setContentScore(contentScore);
        activityComment.setContent(content);
        activityComment.setActivityId(activityId);
        activityComment.setOrganizeScore(organizeScore);

        if (activityApplyService.insertActivityComment(activityComment)) {
            remap.put("status", 200);
            remap.put("msg", "sucess");
            return remap;
        } else {
            remap.put("status", 500);
            remap.put("msg", "评价失败");
            return remap;
        }
    }

    //报名活动
    @AuthToken
    @RequestMapping(value = "join_activity", method = RequestMethod.POST)
    public ResultBean joinActivity(@RequestBody JSONObject jsonObject) {
        Integer activityId = jsonObject.getInteger("activityId");
        String username = jsonObject.getString("username");
        Relation relation = new Relation();
        relation.setApplyId(activityId);
        relation.setUsername(username);
        String formId = jsonObject.getString("formId");
        if (formId != null) {
            wxFunc.saveFormId(username, formId);
        }
        ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);

        if (activityApply.getAdmin2Status() != 1) {
            throw new StatusErrorException(209, "活动未通过审核");
        }
        if (activityApply.getActivityStatus() == 0) {  //这里的ActivityStatus是数据库保存的活动状态，0可报名，1报名结束
            if (activityApplyService.userJoinActivity(relation)) {
                return ResultBean.success();
            } else {
                return ResultBean.error(500, "报名活动失败");
            }
        } else {
            throw new StatusErrorException(210, "已经截止报名");
        }
//        if(func.isEnrollable(activityId)){  //未到报名截止时间
//            if(activityApplyService.userJoinActivity(relation)){
//                return ResultBean.success();
//            }else {
//                return ResultBean.error(500, "报名活动失败");
//            }
//        }else {
//            Integer status = activityApply.getActivityStatus();
//            //开一个线程执行消息发送函数
//            if(status==0){ //状态需要改变了,并且调用发送消息函数
//                new Thread(() -> {
//                    wxFunc.sendActivityUserMsg(activityId);
//                });
//            }
//            throw new StatusErrorException(210,"已经截止报名");
//        }

    }

    //取消活动报名
    @AuthToken
    @RequestMapping(value = "quit_activity")
    public ResultMap quitActivity(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Integer activityId = jsonObject.getInteger("activityId");
        if (activityApplyService.deleteActivityUser(activityId, username)) {
            return ResultMap.success();
        } else {
            return ResultMap.error(500, "取消报名失败");
        }
    }


    //获取我申请的场地
    @AuthToken
    @RequestMapping(value = "get_my_apply_area")
    public ResultObject getMyApplyArea(@RequestBody JSONObject jsonObject) {
        Map data = new HashMap();
        String username = jsonObject.getString("username");
        List<Object> checking = new ArrayList<>();//审核中
        List<Object> passGoing = new ArrayList<>();
        List<Object> noPass = new ArrayList<>();
        List<Object> passFinish = new ArrayList<>();
        List<AreaApply> areaApplies = areaApplyService.getMyAreaApply(username);
        areaApplies.forEach(item -> {
            item.setAreaName(func.areaIdToName(item.getApplyArea()));
            int type = Func.getAreaStatus(item);
            System.out.println(type);
            if (type == 1) {
                item.setApplyStatus(1);
                passGoing.add(item);
            } else if (type == 2) {
                item.setApplyStatus(2);
                passFinish.add(item);
            } else if (type == 3) {
                item.setApplyStatus(3);
                noPass.add(item);
            } else if (type == 4) {
                item.setApplyStatus(4);
                checking.add(item);
            } else {
                throw new RuntimeException("出现未知错误");
            }
        });
        data.put("check", checking);
        data.put("passGoing", passGoing);
        data.put("passFinish", passFinish);
        data.put("noPass", noPass);
        return ResultObject.success(data);
    }

    //获取我申请的活动，场地
    @AuthToken
    @RequestMapping(value = "get_my_apply")
    public ResultObject getMyApply(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Map data = new HashMap();
        List<Object> checking = new ArrayList<>();//审核中
        List<Object> passGoing = new ArrayList<>();
        List<Object> noPass = new ArrayList<>();
        List<Object> passFinish = new ArrayList<>();
        List<ActivityApply> activityApplies = activityApplyService.queryMyApplyActivity(username);
        List<AreaApply> areaApplies = areaApplyService.getMyAreaApply(username);
        activityApplies.forEach(item -> {
            item.setAreaName(func.areaIdToName(item.getApplyArea()));
            int type = Func.getActivityStatus(item);
            System.out.println(type);
            if (type == 1) {
                item.setActivityStatus(1);
                passGoing.add(item);
            } else if (type == 2) {
                item.setActivityStatus(2);
                passFinish.add(item);
            } else if (type == 3) {
                item.setActivityStatus(3);
                noPass.add(item);
            } else if (type == 4) {
                item.setActivityStatus(4);
                checking.add(item);
            } else {
                throw new RuntimeException("出现未知错误");
            }
        });
        areaApplies.forEach(item -> {
            item.setAreaName(func.areaIdToName(item.getApplyArea()));
            int type = Func.getAreaStatus(item);
            if (type == 1) {
                item.setApplyStatus(1);
                passGoing.add(item);
            } else if (type == 2) {
                item.setApplyStatus(2);
                passFinish.add(item);
            } else if (type == 3) {
                item.setApplyStatus(3);
                noPass.add(item);
            } else if (type == 4) {
                item.setApplyStatus(4);
                checking.add(item);
            } else {
                throw new RuntimeException("出现未知错误");
            }
        });
        data.put("check", checking);
        data.put("passGoing", passGoing);
        data.put("passFinish", passFinish);
        data.put("noPass", noPass);
        return ResultObject.success(data);
    }


    //获取我参与的活动   包括"场地申请"
    @AuthToken
    @RequestMapping(value = "get_activity_by_username")
    public ResultObject getApplyByUsername(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        List<Object> finish = new ArrayList();
        List<Object> going = new ArrayList<>();
        Map<String, List> data = new HashMap();
        List<ActivityApply> activityApplies = activityApplyService.getActivityByUsername(username);
        activityApplies.forEach(item -> {
            if (item.getActivityStatus() < 2) {
                int type = Func.getActivityStatus(item);
                logger.info("type:" + type);
                if (type == 1) {
                    item.setActivityStatus(1);
                    if (item.getThumbnail().length() > 0) {
                        item.setThumbnail(Constants.PHOTOROOT + item.getThumbnail());
                    } else {
                        item.setThumbnail(Constants.DEFAULT_IMG_PATH);
                    }
                    item.setAreaName(func.areaIdToName(item.getApplyArea()));
                    going.add(item);
                } else if (type == 2) {
                    item.setActivityStatus(2);
                    if (item.getThumbnail().length() > 0) {
                        item.setThumbnail(Constants.PHOTOROOT + item.getThumbnail());
                    } else {
                        item.setThumbnail(Constants.DEFAULT_IMG_PATH);
                    }
                    item.setAreaName(func.areaIdToName(item.getApplyArea()));
                    finish.add(item);
                }
            }

        });
        List<AreaApply> areaApplies = areaApplyService.getApplyByUsername(username);
        areaApplies.forEach(item -> {
            int type = Func.getAreaStatus(item);
            logger.info("type:" + type);
            if (type == 1) {
                item.setApplyStatus(1);
                item.setAreaName(func.areaIdToName(item.getApplyArea()));
                going.add(item);
            } else if (type == 2) {
                item.setApplyStatus(2);
                item.setAreaName(func.areaIdToName(item.getApplyArea()));
                finish.add(item);
            }
        });
        data.put("finish", finish);
        data.put("going", going);
        return ResultObject.success(data);
    }

    //首页获取活动
    @AuthToken
    @RequestMapping(value = "get_home_activities")
    public ResultBean getHomeActiviyList() {
        Map paraMap = new HashMap<>();
        List<ActivityApply> activityApplies = activityApplyService.queryEnrollApply();
        activityApplies.forEach(item -> {
            item.setAreaName(func.areaIdToName(item.getApplyArea()));
            //item.setStartTime(new Date(Func.getIntTime(item.getStartTime())));
            if (item.getThumbnail().length() > 0) {
                item.setThumbnail(Constants.PHOTOROOT + item.getThumbnail());
            } else {
                item.setThumbnail(Constants.DEFAULT_IMG_PATH);
            }
        });
        return ResultBean.success(activityApplies);
    }

    //获取活动投诉列表
    @AuthToken
    @RequestMapping(value = "get_activity_complaint")
    public ResultObject getActivityComplaint() {
        Long queryTime = Func.getIntTime() - 7 * 24 * 60 * 60;
        List<ActivityComplaint> noCheckComplaint = activityComplaintService.queryActivityComplaint(0, queryTime);
        List<ActivityComplaint> checkedComplaint = activityComplaintService.queryActivityComplaint(2, queryTime);
        Map data = new HashMap();
        noCheckComplaint.forEach(item -> {
            Integer activityId = item.getActivityId();
            item.setActivityApply(activityApplyService.getActivityApplyById(activityId));
            item.setAdminUser(func.usernameToName(item.getUsername()));
        });
        checkedComplaint.forEach(item -> {
            Integer activityId = item.getActivityId();
            item.setActivityApply(activityApplyService.getActivityApplyById(activityId));
            item.setAdminUser(func.usernameToName(item.getAdminUser()));
        });
        data.put("noCheckComplaint", noCheckComplaint);
        data.put("checkedComplaint", checkedComplaint);
        return ResultObject.success(data);
    }


    //活动投诉
    @AuthToken
    @RequestMapping(value = "add_activity_complaint", method = RequestMethod.POST)
    public ResultMap addActivityComplaint(@RequestBody JSONObject jsonObject) {
        Integer activityId = jsonObject.getInteger("activityId");
        String username = jsonObject.getString("username");
        String complaintType = jsonObject.getString("complaintType");
        String complaintReason = jsonObject.getString("complaintReason");
        Long createTime = Func.getIntTime();

        ActivityComplaint activityComplaint = new ActivityComplaint();
        activityComplaint.setActivityId(activityId);
        activityComplaint.setComplaintReason(complaintReason);
        activityComplaint.setComplaintType(complaintType);
        activityComplaint.setCreateTime(createTime);
        activityComplaint.setUsername(username);

        if (activityComplaintService.addActivityComplaint(activityComplaint)) {
            return ResultMap.success();
        } else {
            return ResultMap.error(500, "投诉出错");
        }
    }

    //处理活动投诉
    @AdminAuthToken
    @RequestMapping(value = "handle_complaint", method = RequestMethod.POST)
    public ResultMap handleComplaint(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Integer complaintId = jsonObject.getInteger("complaintId");
        String feedback = jsonObject.getString("feedback");

        if (activityComplaintService.changeStatus(complaintId, 2, feedback, username)) {
            //DO sendMsg() 不用发了
            return ResultMap.success();
        } else {
            return ResultMap.error(500, "处理活动投诉失败");
        }
    }


    /**
     * 活动惩罚措施部分
     *
     * @param activityId
     */
    private void createActivityPunishment(Integer activityId) {
        activityPunishmentService.addActivityPunishment(activityId);
    }

    @AdminAuthToken
    @RequestMapping(value = "get_activity_punishment")
    public ResultObject activityPunishmentList() {
        Long queryTime = Func.getIntTime() - 7 * 24 * 60 * 60;
        Map data = new HashMap();
        List<ActivityPunishment> noCheckComplaints = activityPunishmentService.queryActivityPunishment(0, queryTime);
        List<ActivityPunishment> preCheckComplaints = activityPunishmentService.queryActivityPunishment(1, queryTime);
        List<ActivityPunishment> checkedComplaints = activityPunishmentService.queryActivityPunishment(2, queryTime);
        noCheckComplaints.forEach(item -> {
            item.setActivityApply(activityApplyService.getActivityApplyById(item.getActivityId()));
        });
        preCheckComplaints.forEach(item -> {
            item.setActivityApply(activityApplyService.getActivityApplyById(item.getActivityId()));
        });
        checkedComplaints.forEach(item -> {
            item.setActivityApply(activityApplyService.getActivityApplyById(item.getActivityId()));
        });
        data.put("noCheckPunishment", noCheckComplaints);
        data.put("preCheckPunishment", preCheckComplaints);
        data.put("checkedPunishment", checkedComplaints);
        return ResultObject.success(data);
    }

    @AdminAuthToken
    @RequestMapping(value = "change_punishment_status")
    public ResultMap changeComplaintStatus(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String realName = func.usernameToName(username);
        Integer adminType = func.getAdminType(username);
        String sponsorUser = jsonObject.getString("sponsorUser");
        Integer complaintId = jsonObject.getInteger("complaintId");
        ActivityPunishment activityPunishment = new ActivityPunishment();
        activityPunishment.setId(complaintId);
        activityPunishment.setUpdateTime(Func.getIntTime());


        int expireTime = 0;
        String banTime = jsonObject.getString("banTime");
        if (banTime != null) {
            activityPunishment.setBanTime(banTime);
            if (banTime.equals("no")) {
                expireTime = 100;
            }
            if (banTime.equals("1week")) {
                expireTime = 7 * 24 * 60 * 60;
            }
            if (banTime.equals("2week")) {
                expireTime = 2 * 7 * 24 * 60 * 60;
            }
            if (banTime.equals("1month")) {
                expireTime = 30 * 24 * 60 * 60;
            }
            if (banTime.equals("2month")) {
                expireTime = 2 * 30 * 24 * 60 * 60;
            }
            if (banTime.equals("1term")) {
                expireTime = 4 * 30 * 24 * 60 * 60;
            }
            if (banTime.equals("1year")) {
                expireTime = 12 * 30 * 24 * 60 * 60;
            }
            System.out.println("blacklist:" + sponsorUser);
        } else throw new InputErrorException(260, "未输入禁用时间");

        if (jsonObject.getString("complaintReason") != null) {
            activityPunishment.setComplaintReason(jsonObject.getString("complaintReason"));
        }
        Integer scoreChange = jsonObject.getInteger("scoreChange");
        if (scoreChange != null) {
            activityPunishment.setScoreChange(scoreChange);
        } else new InputErrorException(260, "未输入扣除的积分");
        if (adminType == 0 || adminType == 4) {
            throw new PermissionException(401, "普通用户，权限不足");
        } else if (adminType == 1) { //学生管理员
            activityPunishment.setAdmin1User(realName);
            activityPunishment.setStatus(1);
            if (activityPunishmentService.changeStatus(activityPunishment)) {
                return ResultMap.success();
            } else {
                return ResultMap.success(210, "已处理事件或不存在该事件id");
            }
        } else {  //教师管理员, 执行扣分和禁用操场
            activityPunishment.setAdmin2User(realName);
            activityPunishment.setStatus(2);
            if (activityPunishmentService.changeStatus(activityPunishment)) {
                userService.updatePoints(sponsorUser, scoreChange); //扣分
                CacheUtils.saveString("blacklist:" + sponsorUser, banTime, expireTime);//拉黑
                System.out.println("blacklist:" + sponsorUser);
                return ResultMap.success();
            } else {
                return ResultMap.success(210, "已处理事件或不存在该事件id");
            }
        }
    }

    @Transactional
    @AdminAuthToken
    @RequestMapping(value = "cancel_published_activity")
    public ResultMap cancelPublishedActivity(@RequestBody JSONObject jsonObject) {
        Integer activityId = jsonObject.getInteger("activityId");
        String username = jsonObject.getString("username");
        Integer adminType = func.getAdminType(username);
        if (adminType < 2) {
            throw new PermissionException(401, "没有权限");
        }

        ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
        Integer areaId = activityApply.getApplyArea();
        Date startTime = activityApply.getStartTime();
        Date endTime = activityApply.getEndTime();
        int startTimeIndex = Func.timeToIndex(startTime);
        int endTimeIndex = Func.timeToIndex(endTime);
        String day = Func.getDate(startTime);
        String key = "usage:" + day + ":" + areaId;
        System.out.println(key);
        if (!CacheUtils.isCached(key)) {
            throw new RuntimeException("场地情况丢失");
        }
        String originUsage = CacheUtils.getString(key);
        System.out.println(originUsage);
        String newUsage = Func.replaceOne(new StringBuilder(originUsage), startTimeIndex, endTimeIndex);
        System.out.println(newUsage);
        if (newUsage == null) {
            return ResultMap.error(210, "该活动已取消");
        }
        activityApply.setActivityId(activityId);
        activityApply.setAdmin2Status(2);
        String adminAdvice = jsonObject.getString("adminAdvice");
        if (adminAdvice != null) {
            activityApply.setAdmin2Name(func.usernameToName(username));
            activityApply.setAdmin2Advice(adminAdvice);
        }
        if (activityApplyService.changeStatus(activityApply)) {
            String openId = func.getOpenIdByUsername(activityApply.getApplyUser());
            String formId = wxFunc.getFormId(activityApply.getApplyUser());
            //发送取消活动 推送
            if (openId != null && formId != null) {
                wxFunc.pushActivityApplyFail(openId, formId, activityApply.getTitle(), activityApply.getAdmin2Advice());
            }
            usageRecordService.removeUsageRecord(activityId, 0);
            String key2 = "area_use:" + areaId + ":" + day + ":activity";

            userService.updatePoints(activityApply.getApplyUser(), -5); //扣分
            CacheUtils.setRemove(key2, activityId.toString());
            CacheUtils.saveString(key, newUsage);
            return ResultMap.success();
        } else {
            return ResultMap.error(500, "取消活动失败");
        }

    }


    //生成pdf文件
    @AuthToken
    @RequestMapping(value = "create_pdf")
    public ResultObject createPdf(@RequestBody List<RequestObject> RequestLists) throws FileNotFoundException, DocumentException {
        Document document = new Document(PageSize.A4);
        TestPdf pdf = new TestPdf();
        //"/var/lib/tomcat8/webapps/ROOT/community/pdf_file/testPdf.pdf";
        String fileName = Func.getIntTime() + Func.makeAuthCode() + ".pdf";
        String filePath = Constants.FILEPATH + fileName;
        String getPath = Constants.SERVER_HOST + "/community/pdf_file/" + fileName;
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        RequestLists.forEach(item -> {
            Integer type = item.getType();
            Integer applyId = item.getId();
            try {
                func.addContent(document, applyId, type);
            } catch (Exception e) {
                e.getStackTrace();
            }
        });
        document.close();
        return ResultObject.success(getPath);
    }

    //pdf预览
    @AuthToken
    @RequestMapping(value = "preview_pdf")
    public ResultBean previewPdf(@RequestBody List<RequestObject> RequestLists) {
        ArrayList activityList = new ArrayList();
        RequestLists.forEach(item -> {
            Integer activityId = item.getId();
            ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
            activityApply.setActivityStatus(func.getAverageScore(activityId));
            activityList.add(activityApply);
        });
        return ResultBean.success(activityList);
    }

    @AuthToken
    @Transactional
    @RequestMapping(value = "cancel_activity_apply")
    public ResultMap cancelActivityApply(@RequestBody JSONObject jsonObject) {
        Integer activityId = jsonObject.getInteger("activityId");
        Integer admin2Status = jsonObject.getInteger("admin2Status");
        if (admin2Status == 0) {
            if (activityApplyService.deleteActivityApply(activityId)) {
                return ResultMap.success();
            } else {
                return ResultMap.error(500, "取消活动申请失败");
            }
        } else if (admin2Status == 1) { //审核通过活动取消
            ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
            Integer areaId = activityApply.getApplyArea();
            Date startTime = activityApply.getStartTime();
            Date endTime = activityApply.getEndTime();
            if (Func.getIntTime(startTime) < Func.getIntTime() + 3600 * 24) {
                throw new StatusErrorException(210, "一天之内开始的活动无法取消");
            }
            int startTimeIndex = Func.timeToIndex(startTime);
            int endTimeIndex = Func.timeToIndex(endTime);
            String day = Func.getDate(startTime);
            String key = "usage:" + day + ":" + areaId;
            if (!CacheUtils.isCached(key)) {
                throw new RuntimeException("场地情况丢失");
            }
            String originUsage = CacheUtils.getString(key);
            System.out.println(originUsage);
            String newUsage = Func.replaceOne(new StringBuilder(originUsage), startTimeIndex, endTimeIndex);
            System.out.println(newUsage);
            if (newUsage == null) {
                return ResultMap.error(211, "该场地申请已取消");
            }
            activityApply.setActivityId(activityId);
            activityApply.setAdmin2Status(3);
            if (activityApplyService.changeStatus(activityApply)) {
                usageRecordService.removeUsageRecord(activityId, 0);
                userService.updatePoints(activityApply.getApplyUser(), -5); //扣分
                String key2 = "area_use:" + areaId + ":" + day + ":activity";
                CacheUtils.setRemove(key2, activityId.toString());
                CacheUtils.saveString(key, newUsage);
                return ResultMap.success();
            } else {
                return ResultMap.error(500, "取消活动失败");
            }
        } else {
            throw new StatusErrorException(212, "非审核或通过的活动无法取消");
        }

    }
}

