package com.gdufs.demo.service.impl;

import com.gdufs.demo.dao.ActivityComplaintDao;
import com.gdufs.demo.entity.ActivityComplaint;
import com.gdufs.demo.service.ActivityApplyService;
import com.gdufs.demo.service.ActivityComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityComplaintServiceImpl implements ActivityComplaintService {
    @Autowired
    private ActivityComplaintDao activityComplaintDao;

    @Override
    public Boolean addActivityComplaint(ActivityComplaint activityComplaint) {
        try {
            int effectNum = activityComplaintDao.insertActivityComplaint(activityComplaint);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("投诉失败,请重试");
            }
        } catch (Exception e) {
            throw new RuntimeException("投诉失败,请重试");
        }
    }

    @Override
    public List<ActivityComplaint> queryActivityComplaint(Integer status, Long queryTime) {
        return activityComplaintDao.quertActivityComplaints(status, queryTime);
    }

    @Override
    public Boolean changeStatus(Integer complaintId, Integer status, String feedback, String adminUser) {
        try {
            int effectNum = activityComplaintDao.changeStatus(complaintId, status, feedback, adminUser);
            if (effectNum > 0) {
                return true;
            } else {
                throw new RuntimeException("审核失败,请重试");
            }
        } catch (Exception e) {
            throw new RuntimeException("审核失败,请重试");
        }
    }
}
