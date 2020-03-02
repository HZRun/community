package com.gdufs.demo.scheduling;

import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.Area;
import com.gdufs.demo.service.ActivityApplyService;
import com.gdufs.demo.service.AreaService;
import com.gdufs.demo.service.UserService;
import com.gdufs.demo.utils.Func;
import com.gdufs.demo.utils.WxFunc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyTask {
    public static int day = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyTask.class);

    private static final long SECOND = 1000;

    /**
     * 注入service
     */
    @Autowired
    private AreaService areaService;

    @Autowired
    private ActivityApplyService activityApplyService;
    @Autowired
    private Func func;
    @Autowired
    private WxFunc wxFunc;

    /**
     * 每个小时执行一次
     */
    @Scheduled(cron = "0 1 * * * ?")
    public void changeActiviyStatus() {
        LOGGER.info("执行了整点检测任务");
        Map paraMap = new HashMap();
        paraMap.put("admin2Status", 1);
        paraMap.put("activityStatus", 0);
        List<ActivityApply> activityApplies = activityApplyService.getActivityApplyList(paraMap);
        activityApplies.forEach(item -> {
            if (!func.isEnrollable(item)) {//过了报名时间
                Integer status = item.getActivityStatus();
                System.out.println(status);
                if (status == 0) { //状态需要改变了,并且调用发送消息函数
                    wxFunc.sendActivityUserMsg(item.getActivityId());
                }
            }
        });

    }


}
