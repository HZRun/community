package com.gdufs.demo.dao;

import com.gdufs.demo.entity.Relation;
import com.gdufs.demo.entity.AreaApply;

import java.util.List;

public interface ApplyUserDao {
    int insertApplyUser(Relation relation);

    int updateUserStatus(Relation relation);

    int updateStatus(Integer status, Integer applyId, String username);

    List<Relation> queryApplyUserByApplyId(Integer applyId);

    List<AreaApply> queryApplyUserByUsername(String username);//我参与的"场地申请"

    int deleteAreaApplyUser(String username, Integer applyId);

    Relation queryAreaApplyUserExist(String username, Integer applyId);
}
