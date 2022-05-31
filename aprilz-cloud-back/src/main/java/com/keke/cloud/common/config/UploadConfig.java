package com.keke.cloud.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 上传配置类
 * @author: liushaohui
 * @since: 2022/5/26
 **/
@Configuration
public class UploadConfig {

    public static  String  filePath;

    @Value("${file.path:'\\keke_files'}")
    public void setFilePath(String filePath) {
        UploadConfig.filePath = filePath;
    }
}
