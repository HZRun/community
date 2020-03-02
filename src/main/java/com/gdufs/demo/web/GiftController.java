package com.gdufs.demo.web;

import com.alibaba.fastjson.JSONObject;
import com.gdufs.demo.entity.*;
import com.gdufs.demo.handler.InputErrorException;
import com.gdufs.demo.handler.PermissionException;
import com.gdufs.demo.service.UserService;
import com.gdufs.demo.utils.CacheUtils;
import com.gdufs.demo.utils.Constants;
import com.gdufs.demo.utils.Func;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/gift")
public class GiftController {
    @Autowired
    private Func func;
    @Autowired
    private UserService userService;

    //新增礼物记录
    @RequestMapping(value = "add_gift")
    public ResultMap addGift(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Integer adminType = func.getAdminType(username);
        if (adminType < 2) {
            throw new PermissionException(401, "权限不足");
        }
        String giftName = jsonObject.getString("giftName");
        String introduce = jsonObject.getString("introduce");
        Integer value = jsonObject.getInteger("value");
        Integer remainNum = jsonObject.getInteger("remainNum");
        String redeemLocation = jsonObject.getString("redeemLocation");
        //String giftImage = jsonObject.getString("giftImage");
        String giftImageUrl = Constants.GIFT_DEFAULT_IMAGE;
        if (introduce == null || giftName == null || value == null || remainNum == null || redeemLocation == null) {
            throw new InputErrorException(210, "请输入完整信息");
        }
        if (jsonObject.containsKey("giftImage")) {//上传了图片
            String basePath = Constants.BASEPATH;
            String image = jsonObject.getString("giftImage");
            image = image.substring(image.indexOf(",") + 1);//attention！逗号前的字符不用decode,哭晕在厕所
            String imageName = Func.createImageName();
            String thumbnailFile = "gift_thumbnail/" + imageName + ".jpg";
            String fullFile = "gift_image/" + imageName + ".jpg";
            String thumbFilePath = basePath + thumbnailFile;
            System.out.println(basePath + fullFile);
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                byte[] b = decoder.decodeBuffer(image);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {//调整异常数据
                        b[i] += 256;
                    }
                }
                String fullImagePath = basePath + fullFile;
                OutputStream out = new FileOutputStream(fullImagePath);
                out.write(b);
                out.flush();
                out.close();
                Thumbnails.of(fullImagePath).size(1000, 500).toFile(thumbFilePath);
                String giftRealPath = Constants.PHOTOROOT + thumbnailFile;
                giftImageUrl = giftRealPath;

            } catch (Exception e) {
                throw new RuntimeException("图片上传失败");
            }
        }
        Gift gift = new Gift();
        Long giftId = CacheUtils.incr("next_gift_id");
        gift.setGiftId(giftId);
        gift.setGiftName(giftName);
        gift.setIntroduce(introduce);
        gift.setRedeemLocation(redeemLocation);
        gift.setValue(value);
        gift.setImageUrl(giftImageUrl);
        gift.setRemainNum(remainNum);
        String key = "gift:" + giftId;
        CacheUtils.hashSet("gift", key, gift);
        return ResultMap.success();
    }

    //获得礼物列表
    @RequestMapping(value = "gift_list")
    public ResultObject giftList() {
        Object gifts = CacheUtils.hgetAll("gift");
        return ResultObject.success(gifts);
    }


    @RequestMapping(value = "del_gift")
    public ResultMap delGift(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Integer adminType = func.getAdminType(username);
        if (adminType < 2) {
            throw new PermissionException(401, "权限不足");
        }
        String giftId = jsonObject.getString("giftId");
        CacheUtils.delFromMap("gift", giftId);
        return ResultMap.success();
    }

    //兑换礼品
    @RequestMapping(value = "exchange_gift")
    public ResultMap exchangeGift(@RequestBody JSONObject jsonObject) {
        String giftId = jsonObject.getString("giftId");
        String username = jsonObject.getString("username");
        Gift oldGift = CacheUtils.hashGet("gift", giftId, Gift.class);
        System.out.println(oldGift.getValue());
        Integer remainNum = oldGift.getRemainNum();
        Integer value = oldGift.getValue();
        UserBase user = userService.queryUser(username);
        System.out.println(user.getPoints());
        if (remainNum <= 0) {
            return ResultMap.success(208, "礼品数量不足");
        } else {
            if (user.getPoints() < value) {
                return ResultMap.success(209, "积分不足");
            } else {
                Integer pointNeed = value * (-1);
                userService.updatePoints(username, pointNeed);
                oldGift.setRemainNum(remainNum - 1);
                CacheUtils.hashSet("gift", giftId, oldGift);
                return ResultMap.success();
            }
        }

    }

    //礼品修改
    @RequestMapping(value = "update_gift")
    public ResultMap update(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        Integer adminType = func.getAdminType(username);
        if (adminType < 2) {
            throw new PermissionException(401, "权限不足");
        }
        String giftId = jsonObject.getString("giftId");
        Integer newRemainNum = jsonObject.getInteger("newRemainNum");
        Gift gift = CacheUtils.hashGet("gift", giftId, Gift.class);
        String giftName = jsonObject.getString("giftName");
        String introduce = jsonObject.getString("introduce");
        Integer value = jsonObject.getInteger("value");
        //Integer remainNum = jsonObject.getInteger("remainNum");
        String redeemLocation = jsonObject.getString("redeemLocation");
        if (giftName != null) {
            gift.setGiftName(giftName);
        }
        if (introduce != null) {
            gift.setIntroduce(introduce);
        }
        if (value != null) {
            gift.setValue(value);
        }
        if (newRemainNum != null) {
            gift.setRemainNum(newRemainNum);
        }
        if (redeemLocation != null) {
            gift.setRedeemLocation(redeemLocation);
        }
        CacheUtils.hashSet("gift", giftId, gift);
        return ResultMap.success();
    }


}
