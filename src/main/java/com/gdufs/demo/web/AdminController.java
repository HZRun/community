package com.gdufs.demo.web;


import com.gdufs.demo.entity.Area;
import com.gdufs.demo.entity.ResultBean;
import com.gdufs.demo.service.AreaService;
import com.gdufs.demo.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
/**
 * 项目开发中用到过，在项目运行中不起作用
 */
public class AdminController {
    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "add_usage_by_hand", method = RequestMethod.POST)
    /**
     * 在早期开发中，手动创建场地使用记录，已废弃
     *
     */
    public ResultBean addUsageByHand() {
        for (int i = 0; i < 30; i++) {
            List<Area> areaList = areaService.getArea();
            String date = Func.getDate(i);
            areaList.forEach(item -> {
                areaService.setDateUsage(date, item.getAreaId());
            });
        }
        return ResultBean.success();
    }
}
