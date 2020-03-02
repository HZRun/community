package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityComment;
import com.gdufs.demo.entity.ApplyComment;
import com.gdufs.demo.entity.AreaApply;

import java.util.List;
import java.util.Map;

public interface AreaApplyDao {
    int insertAreaApply(AreaApply areaApply);

    List<AreaApply> queryAreaApply(Map<String, Integer> paraMap);

    AreaApply queryAreaApplyById(int ApplyId);

    int updateApplyStatus(AreaApply areaApply);

    List<AreaApply> queryMyApplyArea(String username);

    int deleteAreaApply(Integer applyId);

    int insertApplyComment(ApplyComment applyComment);

    List<ApplyComment> queryApplyComment();

}
