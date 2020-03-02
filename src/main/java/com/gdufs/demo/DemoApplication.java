package com.gdufs.demo;

import com.gdufs.demo.config.FrameworkBaseConfig;
import com.gdufs.demo.config.FrameworkBaseConstantsHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableScheduling
@EnableCaching  //开启缓存
@SpringBootApplication
@EnableWebSecurity   //开启security
public class DemoApplication {
    public static void main(String[] args) {
        //SpringApplication.run(DemoApplication.class, args);
        ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
    }
}
