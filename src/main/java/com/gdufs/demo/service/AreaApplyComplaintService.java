package com.gdufs.demo.service;

import com.gdufs.demo.entity.ActivityComplaint;
import com.gdufs.demo.entity.AreaApplyComplaint;

import java.util.List;

public interface AreaApplyComplaintService {
    Boolean addAreaApplyComplaint(Integer applyId);

    List<AreaApplyComplaint> queryAreaApplyComplaint(Integer status, Long queryTime);

    Boolean changeStatus(AreaApplyComplaint areaApplyComplaint);

    Boolean deleteAreaApplyComplaint(Integer applyId);

}
