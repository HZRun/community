package com.gdufs.demo.service;

import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Func;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;

public class AreaServiceTest {
    @Autowired
    AreaService areaService;

    @Test
    public void setDateUsageTest() {
        String date = Func.getDate(365);
        System.out.println(date);
//        Date date = new Date();
//        Integer areaId = 1001;
//        String initStatus = "00000000000000000000000000000000";
//        CacheUtils.setSave("usageTest",initStatus);
        //System.out.println(flag);
        //areaService.setDateUsage(date, areaId);
        //assertEquals(flag, true);
    }

}
