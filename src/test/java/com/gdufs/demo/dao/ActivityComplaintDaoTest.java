package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityComplaint;
import com.gdufs.demo.utils.Func;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityComplaintDaoTest {
    @Autowired
    private ActivityComplaintDao activityComplaintDao;

    @Test
    public void insertActvitiyComplaintTest() {
        ActivityComplaint activityComplaint = new ActivityComplaint();
        activityComplaint.setActivityId(16);
        activityComplaint.setComplaintReason("我看发布人不爽，就是投诉他");
        activityComplaint.setComplaintType("发布不当内容");
        activityComplaint.setUsername("20161003456");
        activityComplaint.setCreateTime(Func.getIntTime());
        int effectNum = activityComplaintDao.insertActivityComplaint(activityComplaint);
        assertEquals(effectNum, 1);
    }

    @Test
    public void queryActivityComplaintTest() {
        Integer status = 0;
        Long queryTime = Func.getIntTime() - 7 * 24 * 60 * 60;
        List<ActivityComplaint> activityComplaints = activityComplaintDao.quertActivityComplaints(status, queryTime);
        assertEquals(activityComplaints.get(0).getUsername(), "20161003456");
    }

    @Test
    public void changeStatusTest() {
        Integer status = 2;
        Integer complaintId = 9;
        String feedback = "查证属实，已处理";
        String userAdmin = "20161003456";
        int effectNum = activityComplaintDao.changeStatus(complaintId, status, feedback, userAdmin);
        assertEquals(effectNum, 1);
    }
}
