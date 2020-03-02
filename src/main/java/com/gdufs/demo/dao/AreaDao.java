package com.gdufs.demo.dao;

import com.gdufs.demo.entity.Area;

import java.util.List;

public interface AreaDao {
    List<Area> queryArea();

    Area queryAreaById(Integer areaId);

    int insertArea(Area area);
}
