package com.gdufs.demo.config;

import org.springframework.beans.factory.annotation.Autowired;

public class FrameworkBaseConstantsHelper {
    public static String serverHost;

    @Autowired
    public void setServerHost(FrameworkBaseConfig frameworkBaseConfig) {
        serverHost = frameworkBaseConfig.getHost();
    }

}
