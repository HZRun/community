package com.gdufs.demo.dao;

import com.gdufs.demo.entity.UsageRecord;
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
public class UsageRecordDaoTest {
    @Autowired
    private UsageRecordDao usageRecordDao;

    @Test
    @Ignore
    public void insertUsageRecordTest() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setAdminUser("20161003456");
        usageRecord.setApplyId(3);
        usageRecord.setAreaId(1001);
        usageRecord.setDay("20190529");
        usageRecord.setStartTimeIndex(5);
        usageRecord.setEndTimeIndex(9);
        usageRecord.setType(1);
        int effectNum = usageRecordDao.insertUsageRecord(usageRecord);
    }
}
