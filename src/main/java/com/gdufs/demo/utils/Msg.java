package com.gdufs.demo.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用于发送短信
 */
@Component
public class Msg {
    // 短信应用SDK AppID
    private int appid = 1400202140; // 1400开头(章鱼社团appid)

    // 短信应用SDK AppKey
    private String appkey = "85e3a611715f293c233ab183fb703500";

    // 需要发送短信的手机号码
    private String phoneNumber = "15521426086"; //{"13711489625", "15521426086"};

    // 短信模板ID，需要在短信应用中申请
    private int templateId = 316735; // NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请

    private String[] params = {"", "30"}; //第一个参数是验证码，第二个参数是有效时间，3分钟

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String code) {
        this.params[0] = code;
    }

    // 签名
    private String smsSign = "蝴蝶谷"; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`

    public void sendMsg(String phone, String code) {
        try {
            setParams(code);
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phone,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }
}
