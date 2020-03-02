package com.gdufs.demo.service.impl;

import com.gdufs.demo.dao.AreaDao;
import com.gdufs.demo.entity.Area;
import com.gdufs.demo.service.AreaService;
import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AreaService areaService;

    @Override
    public List<Area> getArea() {
        return areaDao.queryArea();
    }

    @Override
    public Area getAreaById(Integer areaId) {
        return areaDao.queryAreaById(areaId);
    }

    @Override
    public String getAreaUsage(String day, Integer areaId) {
        String key = "usage:" + day + ":" + areaId;
        System.out.println(key);
        String usage = null;
        if (CacheUtils.isCached(key)) {
            usage = CacheUtils.getString(key);
        } else {
            if (CacheUtils.isCached("redis_exist_key")) {//redis状态键，该键存在说明redis没被黑
                List<Area> areaList = areaService.getArea();
                areaList.forEach(item -> {
                    areaService.setDateUsage(day, item.getAreaId());
                });
                usage = CacheUtils.getString(key);
            } else {
                throw new RuntimeException("出现信息丢失错误");
            }
        }
        System.out.println(usage);
        return usage;
    }

    @Override
    public List<Map> getAllAreaUsage(String day) {
        List<Area> area = areaDao.queryArea();
        List<Map> result = new ArrayList<Map>();
        area.forEach(item -> {
            Map map = new HashMap();
            map.put("areaId", item.getAreaId());
            map.put("usage", getAreaUsage(day, item.getAreaId()));
            map.put("capacity", item.getAreaCapacity());
            map.put("areaName", item.getAreaName());
            result.add(map);
        });
        return result;
    }

    @Override
    public boolean changeUsage() {
        return false;
    }

    @Override
    public boolean checkUsage() {
        return false;
    }

    @Override
    public boolean setDateUsage(String day, Integer areaId) {
        String key = "usage:" + day + ":" + areaId;
        System.out.println(key);
        String initStatus = "0000000000000000000000000000000000";
        boolean flag = CacheUtils.isCached(key);
        if (!flag) {
            redisTemplate.opsForValue().set(key, initStatus);
        }
        //System.out.println(flag);
        return true;
    }


}
