package com.gdufs.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
public class FrameworkBaseConfig {

    @Value("${app.host}")
    private String host;

    public String getHost() {
        System.out.println(host);
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
