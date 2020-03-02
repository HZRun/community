package com.gdufs.demo.utils;

import com.alibaba.fastjson.JSONObject;
import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.Relation;
import com.gdufs.demo.entity.TemplateData;
import com.gdufs.demo.entity.WxMsg;
import com.gdufs.demo.service.ActivityApplyService;
import com.gdufs.demo.service.AreaApplyService;
import com.gdufs.demo.service.AreaService;
import com.gdufs.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 有关微信消息推送函数
 */
@Component
public class WxFunc {
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
    @Autowired
    private Func func;

    /**
     * 活动成功举办 发送消息
     *
     * @param activityId
     * @return
     */
    private Logger logger = LoggerFactory.getLogger(WxFunc.class);

    public Boolean pushActivityHostSuccess(Integer activityId) {
        try {
            String accessToken = getAccessToken();
            String url = Constants.PUSH_URL + accessToken;
            ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
            List<Relation> joinUsers = activityApplyService.getActivityUserById(activityId);
            WxMsg wxMsg = new WxMsg();
            wxMsg.setTemplate_id(Constants.TMP_ACTIVITY_HOST_SUCCESS);
            Map map = new HashMap();
            String keyword2 = Func.timeFormatMinute(activityApply.getStartTime()) + "-" + Func.timeFormatMinute(activityApply.getEndTime());
            map.put("keyword1", new TemplateData(activityApply.getTitle()));
            map.put("keyword2", new TemplateData(keyword2));
            map.put("keyword3", new TemplateData(func.areaIdToName(activityApply.getApplyArea())));
            map.put("keyword4", new TemplateData("报名成功，请准时参加"));
            wxMsg.setData(map);
            joinUsers.forEach(item -> {
                String username = item.getUsername();
                String openId = func.getOpenIdByUsername(username);
                String formId = getFormId(username);
                if (openId != null && formId != null) {
                    wxMsg.setTouser(openId);
                    wxMsg.setForm_id(formId);
                    String jsonString = JSONObject.toJSONString(wxMsg);
                    String data = HttpUtil.post(url, jsonString);
                    logger.info("activity-user-sendInfo:" + activityId + " " + username + data);
                }


            });
        } catch (Exception e) {
            logger.info(activityId + ":pushActivityHostSuccess消息发送出错");
        }
        return true;
    }

    /**
     * 活动未能举办 取消活动通知
     *
     * @param activityId
     * @return
     */
    public Boolean pushActivityHostFail(Integer activityId) {
        String accessToken = getAccessToken();
        String url = Constants.PUSH_URL + accessToken;
        ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
        List<Relation> joinUsers = activityApplyService.getActivityUserById(activityId);
        WxMsg wxMsg = new WxMsg();
        wxMsg.setTemplate_id(Constants.TMP_ACTIVITY_HOST_FAIL);
        Map map = new HashMap();
        map.put("keyword1", new TemplateData(activityApply.getTitle()));
        map.put("keyword2", new TemplateData("报名人数不足"));
        map.put("keyword3", new TemplateData("敬请期待下一次的活动"));
        wxMsg.setData(map);
        joinUsers.forEach(item -> {
            String username = item.getUsername();
            String openId = func.getOpenIdByUsername(username);
            String formId = getFormId(username);
            if (openId != null && formId != null) {
                wxMsg.setTouser(openId);
                wxMsg.setForm_id(formId);
                String jsonString = JSONObject.toJSONString(wxMsg);
                String data = HttpUtil.post(url, jsonString);
                logger.info("activity-user-sendInfo:" + activityId + " " + username + data);
            }
        });
        return true;
    }

    /**
     * 活动成功申请 消息发送
     *
     * @param openId
     * @param formId
     * @param activityTitle
     * @return
     */
    public Boolean pushActivityApplySuccess(String openId, String formId, String activityTitle) {
        String accessToken = getAccessToken();
        String url = Constants.PUSH_URL + accessToken;
        WxMsg wxMsg = new WxMsg();
        wxMsg.setTemplate_id(Constants.TMP_ACTIVITY_APPLY_SUCCESS);
        Map map = new HashMap();
        map.put("keyword1", new TemplateData(activityTitle));
        map.put("keyword2", new TemplateData("您的申请已通过"));
        wxMsg.setData(map);
        wxMsg.setTouser(openId);
        wxMsg.setForm_id(formId);
        String jsonString = JSONObject.toJSONString(wxMsg);
        String data = HttpUtil.post(url, jsonString);
        logger.info("pushActivityApplySuccess:" + data);
        return true;
    }

    /**
     * 活动申请失败 消息推送
     *
     * @param openId
     * @param formId
     * @param activityTitle
     * @param failReason
     * @return
     */
    public Boolean pushActivityApplyFail(String openId, String formId, String activityTitle, String failReason) {
        String accessToken = getAccessToken();
        String url = Constants.PUSH_URL + accessToken;
        WxMsg wxMsg = new WxMsg();
        wxMsg.setTemplate_id(Constants.TMP_ACTIVITY_APPLY_FAIL);
        Map map = new HashMap();
        map.put("keyword1", new TemplateData(activityTitle));
        map.put("keyword2", new TemplateData("您的申请未通过"));
        map.put("keyword3", new TemplateData(failReason));
        wxMsg.setData(map);
        wxMsg.setTouser(openId);
        wxMsg.setForm_id(formId);
        String jsonString = JSONObject.toJSONString(wxMsg);
        System.out.println(jsonString);
        String data = HttpUtil.post(url, jsonString);
        System.out.println(data);
        return true;
    }

    /**
     * 场地申请成功 消息推送
     *
     * @param openId
     * @param formId
     * @param areaName
     * @param areaTime
     * @return
     */
    public Boolean pushAreaApplySuccess(String openId, String formId, String areaName, String areaTime) {
        String accessToken = getAccessToken();
        String url = Constants.PUSH_URL + accessToken;
        WxMsg wxMsg = new WxMsg();
        wxMsg.setTemplate_id(Constants.TMP_AREA_APPLY_SUCCESS);
        Map map = new HashMap();
        map.put("keyword1", new TemplateData(areaTime));
        map.put("keyword2", new TemplateData(areaName));
        map.put("keyword3", new TemplateData("场地申请"));
        map.put("keyword4", new TemplateData("已通过审核"));
        wxMsg.setData(map);
        wxMsg.setTouser(openId);
        wxMsg.setForm_id(formId);
        String jsonString = JSONObject.toJSONString(wxMsg);
        System.out.println(jsonString);
        String data = HttpUtil.post(url, jsonString);
        System.out.println(data);
        return true;
    }

    /**
     * 场地申请失败 消息推送
     *
     * @param openId
     * @param formId
     * @param areaName
     * @param areaTime
     * @param failReason
     * @return
     */
    public Boolean pushAreaApplyFail(String openId, String formId, String areaName, String areaTime, String failReason) {
        String accessToken = getAccessToken();
        String url = Constants.PUSH_URL + accessToken;
        WxMsg wxMsg = new WxMsg();
        wxMsg.setTemplate_id(Constants.TMP_AREA_APPLY_FAIL);
        Map map = new HashMap();
        map.put("keyword1", new TemplateData(areaTime));
        map.put("keyword2", new TemplateData(areaName));
        map.put("keyword3", new TemplateData(failReason));
        map.put("keyword4", new TemplateData("未通过审核"));
        wxMsg.setData(map);
        wxMsg.setTouser(openId);
        wxMsg.setForm_id(formId);
        String jsonString = JSONObject.toJSONString(wxMsg);
        System.out.println(jsonString);
        String data = HttpUtil.post(url, jsonString);
        System.out.println(data);
        return true;
    }


    /**
     * 保存formId
     *
     * @param username
     * @param formId
     * @return
     */
    public Boolean saveFormId(String username, String formId) {
        String openId = func.getOpenIdByUsername(username);
        logger.info("openId of " + username + " " + openId);
        String key = "formId:" + openId + ":" + username;
        Long availTime = Func.getIntTime() + 7 * 24 * 60 * 60;
        CacheUtils.saveToSortedset(key, Double.valueOf(availTime), formId);
        if (CacheUtils.isCached(key)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 提取活动formId
     *
     * @param username
     * @return
     */
    public String getFormId(String username) {
        String openId = func.getOpenIdByUsername(username);
        String key = "formId:" + openId + ":" + username;
        Long nowTime = Func.getIntTime();
        Long latestTime = nowTime + 7 * 24 * 60 * 60;

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set sets = zSetOperations.rangeByScore(key, nowTime, latestTime);
//        Set formIds = CacheUtils.listSortedsetRev(key,0 , latestTime.intValue());
        //System.out.println(sets.get(0));
        Iterator it1 = sets.iterator();
        if (it1.hasNext()) {
            String formId = it1.next().toString();
            zSetOperations.remove(key, formId);
            return formId;
        } else {
            return null;
        }
    }


    /**
     * 根据code 获得openId
     *
     * @param code
     * @return
     */
    public static String getOpenId(String code) {
        String url = Constants.CODE_URL;
        String param = "appid=" + Constants.APP_ID + "&secret=" + Constants.APP_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
        String data = HttpUtil.get(url + param);
        JSONObject obj = JSONObject.parseObject(data);
        String openId = obj.getString("openid");
        return openId;
    }

    /**
     * @param wxMsg
     * @return
     */
    public Map<String, Object> getMss(WxMsg wxMsg) {
        Map<String, Object> patientMap = new HashMap<>();
        try {
            //授权（必填）
            String grant_type = "client_credential";
            String appid = "ggggg";
            String appsecret = "aaaaa";
            //URL
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/token";
            //请求参数
            String params = "appid=" + appid + "&secret=" + appsecret + "&grant_type=" + grant_type;
            //发送请求
            //String data = WxHttpUtil.get(requestUrl, params);
            //解析相应内容（转换成json对象）
            //JSONObject json = JSONObject.fromObject(data);
            //String send_Json = Create_Json.send_Json(new Gson().toJson(wxMsg), json.get("access_token").toString());
            //用户的唯一标识（openid）
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            String strbody = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody();
            JSONObject json = JSONObject.parseObject(strbody);
            patientMap.put("responseInfo", json);
            //patientMap.put("ErrorCode", send_Json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return patientMap;
    }

    /**
     * 为参加活动的用户发送消息
     *
     * @param activityId
     */
    public void sendActivityUserMsg(Integer activityId) {
        ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
        List<Relation> relations = activityApplyService.getActivityUserById(activityId);//获得报名人数
        Integer joinNum = relations.size();
        if (joinNum >= activityApply.getMembersLess()) {//满足人数要求
            //TO 发送活动将举报通知
            //System.out.println("发送成功通知...");
            //更改状态
            activityApply.setActivityStatus(1);
            activityApplyService.changeStatus(activityApply);
            userService.updatePoints(activityApply.getApplyUser(), 5);
            logger.info("活动成功举办，申请人加5分");
            pushActivityHostSuccess(activityId);
            logger.info("发送成功通知...");
        } else { //不满足活动人数要求
            //TO 发送活动失败通知
            logger.info("发送失败通知...");
            activityApply.setActivityStatus(2);
            activityApplyService.changeStatus(activityApply);
            pushActivityHostFail(activityId);
        }
    }

    /**
     * 获得accessToken,用于发送推送消息
     * 使用缓存，将accessToken缓存1000秒
     *
     * @return
     */
    public static String getAccessToken() {
        //获取access_token
        String key = "accesstoken";
        if (CacheUtils.isCached(key)) {
            String value = CacheUtils.getString(key);
            return value;
        } else {
            String url = Constants.ACCESS_TOKEN_URL +
                    "&appid=" + Constants.APP_ID + "&secret=" + Constants.APP_SECRET;

            String data = HttpUtil.get(url);
            JSONObject obj = JSONObject.parseObject(data);
            String accessToken = obj.getString("access_token");
            CacheUtils.saveString(key, accessToken, 1000);
            return accessToken;
        }
    }
}
