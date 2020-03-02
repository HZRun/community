package com.gdufs.demo.service;

import com.gdufs.demo.entity.ActivityComplaint;


import java.util.List;

public interface ActivityComplaintService {
    Boolean addActivityComplaint(ActivityComplaint activityComplaint);

    List<ActivityComplaint> queryActivityComplaint(Integer status, Long queryTime);

    Boolean changeStatus(Integer complaintId, Integer status, String feedback, String adminUser);
}
