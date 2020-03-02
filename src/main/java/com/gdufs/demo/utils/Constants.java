package com.gdufs.demo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 一些常数
 */
@Component
public class Constants {
    /*
    图片相关路径
     */
    public static String SERVER_HOST;// = FrameworkBaseConstantsHelper.serverHost;
    //public static final String SERVER_PROJECT_HOST = "https://hzerun.com:8443";
    final static public String PROJECTPATH = "/var/lib/tomcat8/webapps/community/img/";
    final static public String BASEPATH = "/var/lib/tomcat8/webapps/community/img/";
    final static public String FILEPATH = "/var/lib/tomcat8/webapps/community/pdf_file/";
    static public String PHOTOROOT;
    static public String DEFAULT_IMG_PATH;


    static public String GIFT_DEFAULT_IMAGE;
    /*
    微信消息推送相关常量
     */
    //获取access_token
    public final static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";
    //推送url
    public final static String PUSH_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";
    //根据code换取openId
    public final static String CODE_URL = "https://api.weixin.qq.com/sns/jscode2session?";

    public final static String APP_SECRET = "e3b6b3c5318d42b181d08807b16c31cf";

    public static String APP_ID = "wx3b47c8568afd0ed2";

    //场地申请成功
    public static final String TMP_AREA_APPLY_SUCCESS = "2lx8UPwxpbAqn6Wn_zqxEXomZsJCgxF2JO7JWh7dOrs";
    //场地申请失败
    public static final String TMP_AREA_APPLY_FAIL = "t0zfR1FDfca-WBGQPrzKrkf_WISaHuYMGsBZIdUQXvc";

    public static final String TMP_ACTIVITY_APPLY_SUCCESS = "wamVraNZvXX9omBU2ChiwztsuAD9YWWFM9f6C0dy6UU";

    public static final String TMP_ACTIVITY_APPLY_FAIL = "wamVraNZvXX9omBU2ChiwygfI3PchTJGnR_Cu8RxP1g";

    public static final String TMP_ACTIVITY_HOST_SUCCESS = "0ysh3A8N2cUPYcJHkwLkDJlcsMg4gSa2flcamPb_IBo";

    public static final String TMP_ACTIVITY_HOST_FAIL = "OxjfOXAgqB9ad8_jMRiCoEX5UogWYnURBxL8ZcJqpQk";

    /*
    时间长度常量,秒数
     */
    public static final Integer DAY_THREE = 60 * 60 * 24 * 3;
    public static final Integer WEEK_ONE = 60 * 60 * 24 * 7;
    public static final Integer MONTH_ONE = 60 * 60 * 24 * 30;


    @Value("${app.host}")
    public void setServerHost(String serverHost) {
        SERVER_HOST = serverHost;
    }

    @Value("${app.host}")
    public void setPHOTOROOT(String serverHost) {
        PHOTOROOT = serverHost + "/community/img/";
    }

    @Value("${app.host}")
    public void setDefaultImgPath(String serverHost) {
        DEFAULT_IMG_PATH = serverHost + "/community/img/image_file/default_img.jpg";
    }

    @Value("${app.host}")
    public void setGiftDefaultImage(String serverHost) {
        GIFT_DEFAULT_IMAGE = serverHost + "/community/img/image_file/gift_default_img.jpg";
    }
}
