package com.gdufs.demo.service;

import com.gdufs.demo.entity.Area;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AreaService {
    List<Area> getArea();

    Area getAreaById(Integer areaId);

    String getAreaUsage(String day, Integer areaId);

    List<Map> getAllAreaUsage(String day);

    boolean changeUsage();

    boolean checkUsage();

    boolean setDateUsage(String day, Integer areaId); //新建一条redis记录
}
