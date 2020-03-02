package com.gdufs.demo.web;

import com.alibaba.fastjson.JSONObject;
import com.gdufs.demo.annotation.AdminAuthToken;
import com.gdufs.demo.annotation.AuthToken;
import com.gdufs.demo.entity.*;
import com.gdufs.demo.handler.InputErrorException;
import com.gdufs.demo.handler.PermissionException;
import com.gdufs.demo.handler.StatusErrorException;
import com.gdufs.demo.service.*;
import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Constants;
import com.gdufs.demo.utils.Func;
import com.gdufs.demo.utils.WxFunc;
import io.netty.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/area")
public class AreaApplyController {
    @Autowired
    private UserService userService;
    @Autowired
    private AreaApplyComplaintService areaApplyComplaintService;
    @Autowired
    private AreaApplyService areaApplyService;
    @Autowired
    private Func func;
    @Autowired
    private AreaService areaService;
    @Autowired
    private WxFunc wxFunc;
    @Autowired
    private UsageRecordService usageRecordService;

    //场地申请. 只能在小程序端，，即使管理员申请也需要走审核程序
    @AuthToken
    @RequestMapping(value = "apply_area", method = RequestMethod.POST)
    @Transactional
    public Map<String, Object> applyArea(@RequestBody JSONObject jsonObject) {
        Map<String, Object> modelMap = new HashMap<>();
        AreaApply areaApply = new AreaApply();
        Date startTime = jsonObject.getDate("startTime");
        Date endTime = jsonObject.getDate("endTime");
        Integer areaId = jsonObject.getInteger("areaId");
        String username = jsonObject.getString("username");
        String introduce = jsonObject.getString("introduce");
        areaApply.setApplyArea(areaId);
        areaApply.setApplyUser(username);
        areaApply.setStartTime(startTime);
        areaApply.setEndTime(jsonObject.getDate("endTime"));
        areaApply.setIntroduce(jsonObject.getString("introduce"));
        String formId = jsonObject.getString("formId");
        if (formId != null) {
            wxFunc.saveFormId(username, formId);
        }

        if (areaId == null || startTime == null || endTime == null || introduce == null) {
            throw new InputErrorException(260, "输入信息不完整");
        }

        if (Func.checkUserInBlacklist(username)) { //在黑名单中，不允许发帖
            throw new PermissionException(401, "黑名单中用户不允许申请活动");
        }
        if (startTime.compareTo(new Date()) < 0) { //开始时间早于当下时间，不允许
            throw new InputErrorException(240, "开始时间不应早于当下时间");
        } else if (endTime.compareTo(startTime) < 0) {//开始时间早于结束时间
            throw new InputErrorException(240, "结束时间不能早于开始时间");
        }

        //判断是否和场地可用时间冲突
        int startTimeIndex = Func.timeToIndex(startTime);
        int endTimeIndex = Func.timeToIndex(endTime);
        String day = Func.getDate(startTime);
        String key = "usage:" + day + ":" + areaId;
        String originUsage = CacheUtils.getString(key);
        String newUsage = Func.replaceZero(new StringBuilder(originUsage), startTimeIndex, endTimeIndex);
        if (newUsage == null) {  //冲突了
            modelMap.put("status", 230);
            modelMap.put("msg", "所选时间场地不空闲");
            return modelMap;
        }


        //applyUser根据token得到
        //areaApply.setApplyUser(jsonObject.getString("applyUser"));
        if (jsonObject.getString("sponsor") != null) {
            areaApply.setSponsor(jsonObject.getString("sponsor"));
        } else {
            areaApply.setSponsor(func.usernameToName(username));
            //如果不传sponsor过来，取用户名字为主办方
        }
        if (areaApplyService.applyArea(areaApply)) {
            modelMap.put("status", 200);
            modelMap.put("msg", "success");
        } else {
            modelMap.put("status", 500);
            modelMap.put("msg", "fail");
        }
        return modelMap;
    }

    //活动场地申请列表(web端)
    //type 有prePass 预审核通过/pass 通过/noCheck 未审核/[preNoPass 预审核不通过]/noPass 不通过
    @AdminAuthToken
    @RequestMapping(value = "get_area_apply_list", method = RequestMethod.POST)
    public Map<String, Object> getAreaApplyList(@RequestBody JSONObject jsonObject) {
        // 根据admin1_status和admin2_status的不同值，得到不同结果
        // 1. 全为null,获取全部数据
        // 2. admin1Status=1 admin2Status=0 获得预审核通过的数据，等待终审
        // 3. admin2Status=1 获得审核通过的数据
        // 4. admin1Status=0 获得未审核的数据
        // 5. admin1Status=2 获得预审核 不通过的数据
        // 6. admin1Status=1,admin2Status=2 获得预审核通过，终审不通过的数据(不存在了)
        // 7. admin2Status=2 获得终审不通过的数据，可能未经过初审
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
        } else if (type.equals("passNoPass")) { //6 无调用,包含于noPass
            paraMap.put("admin1Status", 1);
            paraMap.put("admin2Status", 2);
        } else if (type.equals("noPass")) {  //7
            paraMap.put("admin2Status", 2);
        } else {
            throw new InputErrorException(260, "没有该活动类型");
        }
        List<AreaApply> areaApplyList = areaApplyService.getAreaApplyList(paraMap);
        areaApplyList.forEach(item -> {
            item.setAreaName(func.areaIdToName(item.getApplyArea()));
            item.setUser(userService.queryUser(item.getApplyUser()));
        });
        remap.put("applyList", areaApplyList);
        remap.put("status", 200);
        remap.put("msg", "success");
        return remap;
    }

    /*
         根据id活动场地信息


     */
    @AuthToken
    @RequestMapping(value = "get_area_apply_by_id", method = RequestMethod.POST)
    public Map<String, Object> getAreaApplyById(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        Integer applyId = jsonObject.getInteger("applyId");
        String username = jsonObject.getString("username");
        String formId = jsonObject.getString("formId");
        if (formId != null) {   //无时不刻收割formId,好狠的鸡哥
            wxFunc.saveFormId(username, formId);
        }
        AreaApply areaApply = areaApplyService.getAreaApplyById(applyId);
        if (areaApply == null) {
            throw new StatusErrorException(220, "数据不存在");
        }
        areaApply.setApplyStatus(Func.getAreaStatus(areaApply));
        areaApply.setAreaName(func.areaIdToName(areaApply.getApplyArea()));
        Integer areaId = areaApply.getApplyArea();
        String areaCapacity = "";
        Integer isJoin = 0;
        if (areaApplyService.queryAreaApplyUserExist(applyId, username)) {
            isJoin = 1;
        }
        List<Relation> relations = areaApplyService.getApplyUserById(applyId);//获得报名人数
        Integer joinNum = relations.size();//报名人数
        if (CacheUtils.isCached("capacity:" + areaId)) {
            areaCapacity = CacheUtils.getString("capacity:" + areaId);
        } else {
            Area area = areaService.getAreaById(areaId);
            areaCapacity = area.getAreaCapacity();
        }
        remap.put("areaApply", areaApply);
        remap.put("areaCapacity", areaCapacity);  //场地容量
        remap.put("isJoin", isJoin);              //"我"是否参加
        remap.put("joinNum", joinNum);            // 报名人数
        remap.put("status", "200");
        return remap;
    }

    /*
       管理员审核场地申请
       根据adminType 得到是学生管理员还是老师管理员
       学生管理员 --> 对admin1Status,admin1Advice,admin1Name 进行操作
       老师管理员 --> 对admin2Status,admin2Advice,admin2Name 进行操作
     */
    @AdminAuthToken
    @RequestMapping(value = "change_status", method = RequestMethod.POST)
    @Transactional
    public ResultBean changeStatus(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        Integer adminStatus = jsonObject.getInteger("adminStatus"); //1通过，2不通过
        String adminAdvice = jsonObject.getString("adminAdvice");
        Integer applyId = jsonObject.getInteger("applyId");
        String username = jsonObject.getString("username");
        String realName = func.usernameToName(username);
        AreaApply applyEntity = new AreaApply();
        applyEntity.setApplyId(applyId);
        Integer adminType = func.getAdminType(username);

        AreaApply areaApply = areaApplyService.getAreaApplyById(applyId);
        String applyUser = areaApply.getApplyUser();
        if (areaApply == null) {
            throw new StatusErrorException(220, "数据不存在");
        }
        Date startTime = areaApply.getStartTime();
        Date endTime = areaApply.getEndTime();
        Integer areaId = areaApply.getApplyArea();
        if (adminType == 0) {  //可不写..AdminAuthToken已经验证过了
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
            if (areaApplyService.changeStatus(applyEntity)) {
                if (adminStatus == 2) { //审批不通过，发送不通过消息
                    String duration = Func.timeFormatMinute(areaApply.getStartTime()) + "-" + Func.timeFormatOnlyMinute(areaApply.getEndTime());
                    String formId = wxFunc.getFormId(applyUser);
                    if (formId != null) {
                        wxFunc.pushAreaApplyFail(func.getOpenIdByUsername(applyUser), formId, func.areaIdToName(areaId), duration, adminAdvice);
                    }

                    // sendAreaFail()
                    return ResultBean.success();
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
                areaApplyService.changeStatus(applyEntity);
                String duration = Func.timeFormatMinute(areaApply.getStartTime()) + "-" + Func.timeFormatMinute(areaApply.getEndTime());
                String formId = wxFunc.getFormId(applyUser);
                if (formId != null) {
                    wxFunc.pushAreaApplyFail(func.getOpenIdByUsername(applyUser), formId, func.areaIdToName(areaId), duration, adminAdvice);
                }

                // sendAreaFail()
                return ResultBean.success();
            } else if (adminStatus == 1) { //审批通过，先判断可用时间是否冲突；
                //检测场地是否空闲
                String key = "usage:" + day + ":" + areaId;
                System.out.println(key);
                if (!CacheUtils.isCached(key)) {
                    throw new RuntimeException("场地情况丢失");
                }
                String originUsage = CacheUtils.getString(key);
//                System.out.println(originUsage);
//                System.out.println(startTimeIndex);
//                System.out.println(endTimeIndex);
                String newUsage = Func.replaceZero(new StringBuilder(originUsage), startTimeIndex, endTimeIndex);
                System.out.println(newUsage);
                if (newUsage != null) { //发送通过消息，修改场地可用时间
                    areaApplyService.changeStatus(applyEntity);
                    createAreaApplyComplaint(applyId);
                    String areaTime = Func.timeFormatMinute(areaApply.getStartTime()) + "-" + Func.timeFormatOnlyMinute(areaApply.getEndTime());
                    String formId = wxFunc.getFormId(applyUser);
                    UsageRecord usageRecord = new UsageRecord();
                    usageRecord.setAdminUser(username);
                    usageRecord.setApplyId(applyId);
                    usageRecord.setAreaId(areaId);
                    usageRecord.setDay(day);
                    usageRecord.setStartTimeIndex(startTimeIndex);
                    usageRecord.setEndTimeIndex(endTimeIndex);
                    usageRecord.setType(1);
                    usageRecordService.insertUsageRecord(usageRecord);
                    String key2 = "area_use:" + areaId + ":" + day + ":apply";
                    CacheUtils.saveToSet(key2, applyId.toString());
                    if (formId != null) {
                        wxFunc.pushAreaApplySuccess(func.getOpenIdByUsername(applyUser), formId, func.areaIdToName(areaApply.getApplyArea()), areaTime);
                        userService.updatePoints(applyUser, 5);
                    }
                    CacheUtils.saveString(key, newUsage);
                    return ResultBean.success();
                    //TO sendAreaApplySuccess()
                } else {
                    throw new RuntimeException("场地可用时间冲突");
                }
            }

        }
        return ResultBean.success();
    }


    //管理员获取活动评价
    @AdminAuthToken
    @RequestMapping(value = "get_apply_comments", method = RequestMethod.POST)
    public ResultObject getApplyComments() {
        List<ApplyComment> applyComments = areaApplyService.getApplyComment();
        applyComments.forEach(item -> {
            item.setAreaApply(areaApplyService.getAreaApplyById(item.getApplyId()));
        });
        return ResultObject.success(applyComments);
    }

    //活动评价
    @RequestMapping(value = "post_apply_comment", method = RequestMethod.POST)
    public Map<String, Object> postApplyComment(@RequestBody JSONObject jsonObject) {
        Map<String, Object> remap = new HashMap<>();
        Integer applyId = jsonObject.getInteger("applyId");
        String usename = jsonObject.getString("username");
        Integer contentScore = jsonObject.getInteger("contentScore");
        Integer organizeScore = jsonObject.getInteger("organizeScore");
        Integer useScore = jsonObject.getInteger("useScore");
        String content = jsonObject.getString("content");
        Long createTime = new Date().getTime() / 1000;

        ApplyComment applyComment = new ApplyComment();
        applyComment.setUsername(usename);
        applyComment.setUseScore(useScore);
        applyComment.setCreateTime(createTime);
        applyComment.setContentScore(contentScore);
        applyComment.setContent(content);
        applyComment.setApplyId(applyId);
        applyComment.setOrganizeScore(organizeScore);

        if (areaApplyService.insertApplyComment(applyComment)) {
            remap.put("status", 200);
            remap.put("msg", "sucess");
            return remap;
        } else {
            remap.put("status", 500);
            remap.put("msg", "评价失败");
            return remap;
        }
    }


    @AuthToken
    //报名 "场地申请"
    @RequestMapping(value = "join_apply")
    public ResultBean joinApply(@RequestBody JSONObject jsonObject) {
        Integer applyId = jsonObject.getInteger("applyId");
        String username = jsonObject.getString("username");
        String formId = jsonObject.getString("formId");
        if (formId != null) {
            wxFunc.saveFormId(username, formId);
        }
        AreaApply areaApply = areaApplyService.getAreaApplyById(applyId);
        if (areaApply == null) {
            new StatusErrorException(208, "场地申请不存在或已取消");
        } else {
            if (areaApply.getAdmin2Status() != 1) {
                throw new StatusErrorException(209, "未审核通过场地申请不能报名");
            }
        }

        Relation relation = new Relation();
        relation.setApplyId(applyId);
        relation.setUsername(username);
        if (areaApplyService.userJoinApply(relation)) {
            return ResultBean.success();
        } else {
            return ResultBean.error(500, "报名活动失败");
        }

    }

    //取消场地申请报名
    @AuthToken
    @RequestMapping(value = "quit_area_apply")
    public ResultMap quitApply(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Integer applyId = jsonObject.getInteger("applyId");
        if (areaApplyService.deleteAreaApplyUser(applyId, username)) {
            return ResultMap.success();
        } else {
            return ResultMap.error(500, "取消报名失败");
        }
    }


    //编辑活动参与人名单
    @AuthToken
    @RequestMapping(value = "update_join_list")
    public ResultMap updateJoinList(@RequestBody List<Relation> relations) {
        System.out.println("kikkos");
//        relations.forEach(item->{
//            Boolean flag = areaApplyService.updateApplyUserStatus(item);
//        });
        if (areaApplyService.updateApplyUserStatus(relations)) {
            return ResultMap.success();
        } else {
            return ResultMap.error(500, "更改状态失败");
        }

    }


    //根据场地活动id获取报名人员
    @RequestMapping(value = "get_apply_user_by_id")
    @AuthToken
    public ResultBean getApplyUserById(@RequestBody JSONObject jsonObject) {
        Integer applyId = jsonObject.getInteger("applyId");
        List<Relation> relations = areaApplyService.getApplyUserById(applyId);
        relations.forEach(item -> {
            item.setRealName(func.usernameToName(item.getUsername()));
        });
        return ResultBean.success(relations);
    }

    // 生成场地惩罚信息
    private void createAreaApplyComplaint(Integer applyId) {
        areaApplyComplaintService.addAreaApplyComplaint(applyId);
    }

    @RequestMapping(value = "get_area_apply_complaint")
    @AdminAuthToken
    public ResultObject areaApplyComplaintList() {
        Long queryTime = Func.getIntTime() - 7 * 24 * 60 * 60;
        Map data = new HashMap();
        List<AreaApplyComplaint> noCheckComplaints = areaApplyComplaintService.queryAreaApplyComplaint(0, queryTime);
        List<AreaApplyComplaint> preCheckComplaints = areaApplyComplaintService.queryAreaApplyComplaint(1, queryTime);
        List<AreaApplyComplaint> checkedComplaints = areaApplyComplaintService.queryAreaApplyComplaint(2, queryTime);
        noCheckComplaints.forEach(item -> {
            if (areaApplyService.getAreaApplyById(item.getApplyId()) != null) {
                item.setAreaApply(areaApplyService.getAreaApplyById(item.getApplyId()));
            }
        });
        preCheckComplaints.forEach(item -> {
            if (areaApplyService.getAreaApplyById(item.getApplyId()) != null) {
                item.setAreaApply(areaApplyService.getAreaApplyById(item.getApplyId()));
            }
        });
        checkedComplaints.forEach(item -> {
            if (areaApplyService.getAreaApplyById(item.getApplyId()) != null) {
                item.setAreaApply(areaApplyService.getAreaApplyById(item.getApplyId()));
            }
        });
        data.put("noCheckComplaint", noCheckComplaints);
        data.put("preCheckComplaint", preCheckComplaints);
        data.put("checkedComplaint", checkedComplaints);
        return ResultObject.success(data);
    }


    @AdminAuthToken
    @Transactional
    @RequestMapping(value = "change_complaint_status")
    public ResultMap changeComplaintStatus(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String realName = func.usernameToName(username);
        Integer adminType = func.getAdminType(username);
        String sponsorUser = jsonObject.getString("sponsorUser");
        Integer complaintId = jsonObject.getInteger("complaintId");
        AreaApplyComplaint areaApplyComplaint = new AreaApplyComplaint();
        areaApplyComplaint.setId(complaintId);
        areaApplyComplaint.setUpdateTime(Func.getIntTime());

        int expireTime = 0;
        String banTime = jsonObject.getString("banTime");
        if (banTime != null) {
            areaApplyComplaint.setBanTime(banTime);
            if (banTime.equals("no")) {
                expireTime = 1;
            }
            if (banTime.equals("1week")) {
                expireTime = Constants.WEEK_ONE;
            }
            if (banTime.equals("2week")) {
                expireTime = 2 * 7 * 24 * 60 * 60;
            }
            if (banTime.equals("1month")) {
                expireTime = Constants.MONTH_ONE;
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
            areaApplyComplaint.setComplaintReason(jsonObject.getString("complaintReason"));
        }
        Integer scoreChange = jsonObject.getInteger("scoreChange");
        if (scoreChange != null) {
            areaApplyComplaint.setScoreChange(scoreChange);
        } else new InputErrorException(260, "未输入扣除的积分");

        if (adminType == 0) {
            throw new PermissionException(401, "普通用户，权限不足");
        } else if (adminType == 1) { //学生管理员
            areaApplyComplaint.setAdmin1User(realName);
            areaApplyComplaint.setStatus(1);
            if (areaApplyComplaintService.changeStatus(areaApplyComplaint)) {
                return ResultMap.success();
            } else {
                return ResultMap.success(210, "已处理事件或不存在该事件id");
            }
        } else {  //教师管理员, 执行扣分和禁用操场
            areaApplyComplaint.setAdmin2User(realName);
            areaApplyComplaint.setStatus(2);
            if (areaApplyComplaintService.changeStatus(areaApplyComplaint)) {
                CacheUtils.saveString("blacklist:" + sponsorUser, banTime, expireTime);//拉黑
                userService.updatePoints(sponsorUser, scoreChange); //扣分
                return ResultMap.success();
            } else {
                return ResultMap.success(210, "已处理事件或不存在该事件id");
            }
        }
    }


    //根据用户名获取参与的"场地活动"
//    @RequestMapping(value = "get_apply_by_username")
//    public ResultBean getApplyByUsername(@RequestBody JSONObject jsonObject){
//        String username = jsonObject.getString("username");
//        List<AreaApply> areaApplies = areaApplyService.getApplyByUsername(username);
//        areaApplies.forEach(item->{
//            item.setApplyStatus(Func.getAreaStatus(item));
//
//        });
//        return ResultBean.success(areaApplies);
//    }

    //取消已发布场地申请
    @AdminAuthToken
    @Transactional
    @RequestMapping(value = "cancel_published_area")
    public ResultMap cancerPublishedArea(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Integer adminType = func.getAdminType(username);
        if (adminType < 2) {
            throw new PermissionException(401, "没有权限");
        }
        Integer applyId = jsonObject.getInteger("applyId");
        AreaApply areaApply = areaApplyService.getAreaApplyById(applyId);
        if (areaApply == null) {
            throw new StatusErrorException(209, "该场地申请已删除");
        }
        areaApply.setApplyId(applyId);
        areaApply.setAdmin2Status(2);
        Integer areaId = areaApply.getApplyArea();
        Date startTime = areaApply.getStartTime();
        Date endTime = areaApply.getEndTime();
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
            return ResultMap.error(210, "该场地申请已取消");
        }
        String adminAdvice = jsonObject.getString("adminAdvice");
        if (adminAdvice != null) {
            areaApply.setAdmin2Advice(adminAdvice);
            areaApply.setAdmin2Name(func.usernameToName(username));
        }
        areaApplyService.changeStatus(areaApply);
        usageRecordService.removeUsageRecord(applyId, 1);
        //场地申请成功后加的分扣回去
        userService.updatePoints(areaApply.getApplyUser(), -5); //扣分
        String duration = Func.timeFormatMinute(areaApply.getStartTime()) + "-" + Func.timeFormatOnlyMinute(areaApply.getEndTime());
        //发送不通过消息
        String openId = func.getOpenIdByUsername(areaApply.getApplyUser());
        String formId = wxFunc.getFormId(areaApply.getApplyUser());
        if (openId != null && formId != null) {
            wxFunc.pushAreaApplyFail(openId, formId, func.areaIdToName(areaId), duration, adminAdvice);
        }
        String key2 = "area_use:" + areaId + ":" + day + ":apply";

        CacheUtils.setRemove(key2, applyId.toString());
        CacheUtils.saveString(key, newUsage);
        return ResultMap.success();
    }

    @Transactional
    @RequestMapping(value = "cancel_area_apply")
    @AuthToken
    public ResultMap cancelAreaApply(@RequestBody JSONObject jsonObject) {
        Integer applyId = jsonObject.getInteger("applyId");
        Integer admin2Status = jsonObject.getInteger("admin2Status");
        String username = jsonObject.getString("username");
        if (admin2Status == 0) { //待审核场地申请
            if (areaApplyService.deleteAreaApply(applyId)) {
                return ResultMap.success();
            } else {
                return ResultMap.error(500, "取消活动申请失败");
            }
        } else if (admin2Status == 1) { //已发布 场地申请
            AreaApply areaApply = areaApplyService.getAreaApplyById(applyId);
            Integer areaId = areaApply.getApplyArea();
            Date startTime = areaApply.getStartTime();
            if (Func.getIntTime(startTime) < Func.getIntTime() + 3600 * 24) {
                throw new StatusErrorException(210, "一天之内开始的活动无法取消");
            }
            Date endTime = areaApply.getEndTime();
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

            areaApplyComplaintService.deleteAreaApplyComplaint(applyId);
            areaApplyService.deleteAreaApply(applyId);

            usageRecordService.removeUsageRecord(applyId, 1);
            userService.updatePoints(areaApply.getApplyUser(), -5); //扣分
            String key2 = "area_use:" + areaId + ":" + day + ":apply";
            CacheUtils.setRemove(key2, applyId.toString());
            System.out.println("修改redis前");
            CacheUtils.saveString(key, newUsage);
            System.out.println("修改rediss后");
        } else {
            throw new StatusErrorException(212, "非审核或通过的活动无法取消");
        }
        return ResultMap.success();
    }


    //测试所用接口，废弃
    @RequestMapping(value = "set_value")
    public ResultBean setValue(@RequestBody JSONObject jsonObject) {
        String date = jsonObject.getString("day");
        String value = jsonObject.getString("value");
        Integer areaId = 1001;
        String initStatus = "00000000000000000000000000000000";
        String day = Func.getDate(1);
        String key = "usage:" + day + ":" + areaId;
        CacheUtils.setSave(key, initStatus);
        return ResultBean.success();
    }

}
