package com.gdufs.demo.service;

import com.gdufs.demo.entity.*;

import java.util.List;
import java.util.Map;

public interface AreaApplyService {
    Boolean applyArea(AreaApply areaApply);

    List<AreaApply> getAreaApplyList(Map<String, Integer> paraMap);

    AreaApply getAreaApplyById(Integer applyId);

    Boolean changeStatus(AreaApply areaApply);

    Boolean userJoinApply(Relation relation);

    Boolean updateApplyUserStatus(List<Relation> relation);

    List<Relation> getApplyUserById(Integer applyId);

    List<AreaApply> getApplyByUsername(String username);

    Boolean insertApplyComment(ApplyComment applyComment);

    List<ApplyComment> getApplyComment();

    List<AreaApply> getMyAreaApply(String username);

    Boolean deleteAreaApply(Integer applyId);

    Boolean deleteAreaApplyUser(Integer applyId, String username);

    Boolean queryAreaApplyUserExist(Integer applyId, String username);


}
