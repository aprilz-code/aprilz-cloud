package com.keke.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @auther flk
 * @create 2020/9/21
 */
@SpringBootApplication
@MapperScan("com.keke.cloud.web.mapper")
public class CloudApp {
    public static void main(String[] args) {
        SpringApplication.run(CloudApp.class,args);
    }

}
