package com.gdufs.demo.dao;

import com.gdufs.demo.entity.Area;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaDaoTest {
    @Autowired
    private AreaDao areaDao;

    @Ignore
    @Test
    public void insertArea() {
        Area area = new Area();
        area.setAreaId(1002);
        area.setAreaName("移动课堂(行远堂)");
        area.setAreaCapacity("10-30");
        int effectNum = areaDao.insertArea(area);
        assertEquals(effectNum, 1);
    }

    @Test
    public void queryArea() {
        List<Area> area = areaDao.queryArea();
        assertEquals(area.get(0).getAreaCapacity(), "70-80");
    }

    @Test
    public void queryAreaById() {
        Area area = areaDao.queryAreaById(1002);
        assertEquals(area.getAreaCapacity(), "10-30");
    }
}
