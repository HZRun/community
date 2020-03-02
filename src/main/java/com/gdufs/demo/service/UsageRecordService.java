package com.gdufs.demo.service;

import com.gdufs.demo.entity.UsageRecord;

public interface UsageRecordService {
    Boolean insertUsageRecord(UsageRecord usageRecord);

    Boolean removeUsageRecord(Integer applyId, Integer type);
}
