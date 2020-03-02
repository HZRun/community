package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityComplaint;
import com.gdufs.demo.entity.AreaApplyComplaint;

import java.util.List;

public interface AreaApplyComplaintDao {
    int insertAreaApplyComplaint(Integer applyId, Long updateTime);

    List<AreaApplyComplaint> quertAreaApplyComplaints(Integer status, Long queryTime);

    int changeStatus(AreaApplyComplaint areaApplyComplaint);

    int romoveAreaApplyComplaint(Integer applyId);
}
