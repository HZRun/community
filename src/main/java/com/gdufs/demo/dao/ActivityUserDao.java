package com.gdufs.demo.dao;

import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.entity.AreaApply;
import com.gdufs.demo.entity.Relation;

import java.util.List;

public interface ActivityUserDao {
    int insertActivityUser(Relation relation);

    List<Relation> queryActivityUserByApplyId(Integer applyId);

    List<ActivityApply> queryActivityUserByUsername(String username);//我参与的活动

    int deleteActivityUser(String username, Integer applyId);

    Relation queryActivityUserExist(String username, Integer applyId);
}
