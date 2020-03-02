package com.gdufs.demo.dao;

import com.gdufs.demo.entity.UsageRecord;
import com.gdufs.demo.entity.User;

public interface UsageRecordDao {
    int insertUsageRecord(UsageRecord usageRecord);

    int removeUsageRecord(Integer applyId, Integer type);
}
