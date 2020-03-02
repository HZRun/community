package com.gdufs.demo.web;

import com.alibaba.fastjson.JSONObject;
import com.gdufs.demo.annotation.AuthToken;
import com.gdufs.demo.entity.Area;
import com.gdufs.demo.entity.ResultBean;
import com.gdufs.demo.entity.ResultObject;
import com.gdufs.demo.service.AreaService;
import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/area_usage")
public class AreaController {
    @Autowired
    private AreaService areaService;
    @Autowired
    private RedisTemplate redisTemplate;

    //获得场地使用情况
    @AuthToken
    @RequestMapping(value = "get_area_usage")
    public ResultBean getAreaUsage(@RequestBody JSONObject jsonObject) {
        Date date = jsonObject.getDate("date");
        String day = Func.getDate(date);
        Integer areaId = jsonObject.getInteger("areaId");
        System.out.println(areaId);
        if (areaId == null) {
            List<Map> map = areaService.getAllAreaUsage(day);
            return ResultBean.success(map);
        } else {
            String usage = areaService.getAreaUsage(day, areaId);
            List<Map> arrayList = new ArrayList<>();
            Map map = new HashMap();
            Area item = areaService.getAreaById(areaId);
            map.put("areaId", areaId);
            map.put("usage", usage);
            map.put("capacity", item.getAreaCapacity());
            map.put("areaName", item.getAreaName());
            arrayList.add(map);
            return ResultBean.success(arrayList);
        }

    }


    @AuthToken
    @RequestMapping(value = "/get_time")
    public ResultObject getTime() {
        Long nowTime = Func.getIntTime();
        return ResultObject.success(nowTime);
    }

    //测试所用
    @RequestMapping(value = "/test")
    public ResultBean test_redis() {
        // redisTemplate.opsForValue().set("test:ojbk:123:12", "00000000000000000000000000000000");
        String day = "20161012";
        String areaId = "1001";
        String key = "usage:" + day + ":" + areaId;

        System.out.println(key);
        //CacheUtils.saveNX("aaaiu", "ok");
        boolean flag = CacheUtils.isCached("usage:20190514:1003");
        if (!flag) {
            redisTemplate.opsForValue().set("usage:20190514:1003", "111111");
        }
        String aa = CacheUtils.getString("usage:20190514:1003");
        System.out.println(aa);
        return ResultBean.success();
    }
}
