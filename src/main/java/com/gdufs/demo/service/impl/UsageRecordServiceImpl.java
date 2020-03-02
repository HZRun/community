package com.gdufs.demo.service.impl;

import com.gdufs.demo.dao.UsageRecordDao;
import com.gdufs.demo.entity.UsageRecord;
import com.gdufs.demo.service.UsageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsageRecordServiceImpl implements UsageRecordService {
    @Autowired
    private UsageRecordDao usageRecordDao;

    @Override
    public Boolean insertUsageRecord(UsageRecord usageRecord) {
        int effectNum = usageRecordDao.insertUsageRecord(usageRecord);
        if (effectNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean removeUsageRecord(Integer applyId, Integer type) {
        int effectNum = usageRecordDao.removeUsageRecord(applyId, type);
        if (effectNum > 0) {
            return true;
        } else {
            return false;
        }
    }
}
