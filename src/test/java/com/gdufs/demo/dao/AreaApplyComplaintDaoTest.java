package com.gdufs.demo.dao;

import com.gdufs.demo.entity.AreaApplyComplaint;
import com.gdufs.demo.utils.Func;
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
public class AreaApplyComplaintDaoTest {
    @Autowired
    private AreaApplyComplaintDao areaApplyComplaintDao;

    @Test
    @Ignore
    public void insertAreaApplyComlaintTest() {
        Long updateTime = Func.getIntTime();
        Integer applyId = 20;
        int effectNum = areaApplyComplaintDao.insertAreaApplyComplaint(applyId, updateTime);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void queryAreaApplyComplaintTest() {
        Integer status = 0;
        Long queryTime = Func.getIntTime() - 60 * 60 * 24 * 7;
        List<AreaApplyComplaint> areaApplyComplaints = areaApplyComplaintDao.quertAreaApplyComplaints(status, queryTime);
        assertEquals(areaApplyComplaints.isEmpty(), false);
    }

    @Test
    @Ignore
    public void changeStatusTest1() {
        AreaApplyComplaint areaApplyComplaint = new AreaApplyComplaint();
        Integer complaintId = 3;
        Integer status = 1;
        Integer scoreChange = -5;
        String complaintReason = "损坏公物";
        String banTime = "1week";
        String admin1User = "管理1";
        areaApplyComplaint.setId(complaintId);
        areaApplyComplaint.setStatus(status);
        areaApplyComplaint.setScoreChange(scoreChange);
        areaApplyComplaint.setComplaintReason(complaintReason);
        areaApplyComplaint.setBanTime(banTime);
        areaApplyComplaint.setAdmin1User(admin1User);
        int effectNum = areaApplyComplaintDao.changeStatus(areaApplyComplaint);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void changeStatusTest2() {
        AreaApplyComplaint areaApplyComplaint = new AreaApplyComplaint();
        Integer complaintId = 3;
        String admin2User = "老员";
        Integer status = 2;
        areaApplyComplaint.setStatus(status);
        areaApplyComplaint.setAdmin2User(admin2User);
        areaApplyComplaint.setId(complaintId);
        int effectNum = areaApplyComplaintDao.changeStatus(areaApplyComplaint);
        assertEquals(effectNum, 1);
    }

    @Test
    @Ignore
    public void deleteAreaApplyComplaint() {
        Integer applyId = 119;
        int effectNum = areaApplyComplaintDao.romoveAreaApplyComplaint(applyId);
        assertEquals(effectNum, 1);
    }
}
