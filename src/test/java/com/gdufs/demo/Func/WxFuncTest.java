package com.gdufs.demo.Func;

import com.gdufs.demo.service.ActivityApplyService;
import com.gdufs.demo.utils.Func;
import com.gdufs.demo.utils.WxFunc;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WxFuncTest {
    @Autowired
    private Func func;
    @Autowired
    private ActivityApplyService activityApplyService;
    @Autowired
    private WxFunc wxFunc;

    @Test
    @Ignore
    public void testActivityApplySuccess() {
        String formId = "5d03a10d916545a3bb4f3f52937c4eb0";
        String openId = "o7WZN5WE3p_tUO0S2yckpjM3zrbw";
        String activityTitle = "信息学院辩论比赛";
        wxFunc.pushActivityApplySuccess(openId, formId, activityTitle);
    }

    @Test
    @Ignore
    //problem
    public void pushActivityApplyFail() {
        String formId = "8a4882dab64443bbb64ea460c00e0668";
        String openId = "o7WZN5WE3p_tUO0S2yckpjM3zrbw";
        String activityTitle = "信息学院辩论比赛";
        String failReason = "拒绝举报没有理由";
        wxFunc.pushActivityApplyFail(openId, formId, activityTitle, failReason);
    }

    @Test
    public void pushAreaApplyFail() {
        String formId = "8a4882dab64443bbb64ea460c00e0668";
        String openId = "o7WZN5WE3p_tUO0S2yckpjM3zrbw";
        String time = "2019/5/24 21:00-2019/5/24 21:30";
        String areaName = "信息学院辩论比赛";
        String failReason = "拒绝举报没有理由";
        wxFunc.pushAreaApplyFail(openId, formId, areaName, time, failReason);
    }

    @Test
    @Ignore
    public void pushAreaApplySuccess() {
        String formId = "56bbb31d7aef49998bc0229a6b1b3dd6";
        String openId = "o7WZN5WE3p_tUO0S2yckpjM3zrbw";
        String time = "2019/5/24 21:00-2019/5/24 21:30";
        String areaName = "信息学院辩论比赛";
        wxFunc.pushAreaApplySuccess(openId, formId, areaName, time);
    }

    @Test
    @Ignore
    public void pushActivityHostSuccess() {
        Integer activityId = 90;
        wxFunc.pushActivityHostSuccess(activityId);
    }

    @Test
    @Ignore
    public void pushActivityHostFail() {
        Integer activityId = 90;
        wxFunc.pushActivityHostFail(activityId);
    }
}
