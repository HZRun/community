package com.gdufs.demo.service.impl;

import com.gdufs.demo.dao.AreaApplyComplaintDao;
import com.gdufs.demo.entity.AreaApplyComplaint;
import com.gdufs.demo.service.AreaApplyComplaintService;
import com.gdufs.demo.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaApplyComplaintServiceImpl implements AreaApplyComplaintService {
    @Autowired
    private AreaApplyComplaintDao areaApplyComplaintDao;

    @Override
    public Boolean addAreaApplyComplaint(Integer applyId) {
        try {
            Long updateTime = Func.getIntTime();
            int effectNum = areaApplyComplaintDao.insertAreaApplyComplaint(applyId, updateTime);
            if (effectNum > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("插入场地投诉失败");
        }
    }

    @Override
    public List<AreaApplyComplaint> queryAreaApplyComplaint(Integer status, Long queryTime) {
        return areaApplyComplaintDao.quertAreaApplyComplaints(status, queryTime);
    }

    @Override
    public Boolean changeStatus(AreaApplyComplaint areaApplyComplaint) {
        try {
            int effctNum = areaApplyComplaintDao.changeStatus(areaApplyComplaint);
            System.out.println(effctNum);
            if (effctNum > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("执行惩罚措施失败");
        }
    }

    @Override
    public Boolean deleteAreaApplyComplaint(Integer applyId) {
        int effectNum = areaApplyComplaintDao.romoveAreaApplyComplaint(applyId);
        if (effectNum > 0) {
            return true;
        } else {
            throw new RuntimeException("取消申请失败");
        }
    }
}
