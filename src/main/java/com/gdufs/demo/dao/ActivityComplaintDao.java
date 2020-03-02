package com.gdufs.demo.dao;


import com.gdufs.demo.entity.ActivityComplaint;

import java.util.List;

public interface ActivityComplaintDao {
    int insertActivityComplaint(ActivityComplaint activityComplaint);

    List<ActivityComplaint> quertActivityComplaints(Integer status, Long queryTime);

    int changeStatus(Integer complaintId, Integer status, String feedback, String adminUser);
}
