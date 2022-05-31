# aprilz-cloud-back

#### 介绍

[aprilz-cloud](https://github.com/liushaohui1/aprilz-cloud.git)

简简单单的网盘系统--后端

简单实现文件分片上传、秒传及断点续传的全局上传。  

主要用来学习[vue-simple-uploader](https://github.com/simple-uploader/vue-uploader/blob/master/README_zh-CN.md )插件


#### 软件架构
软件架构说明  
SpringBoot2 + Vue2  
其他：swagger、 mysql、 shiro、 mybatisPlus

#### 安装教程

1. 打开sql/keke-file.sql,复制sql建库运行
2. 修改application.yml中的数据库账号与密码
3. maven下包完成后，运行CloudApp.java下的main程序即可

上传的文件默认保存在类路径下： application.yml file.path 以及nginx中，需要同步修改

![img.png](image/img.png)
#### 使用说明

1. 登录界面
   ![Image text](image/Snipaste_2022-03-01_15-04-44.png)
2. 首页
   ![Image text](image/Snipaste_2022-03-01_14-57-06.png)
   ![Image text](image/Snipaste_2022-03-01_14-57-41.png)
   ![Image text](image/Snipaste_2022-03-01_14-57-48.png)
   ![Image text](image/Snipaste_2022-03-01_14-58-05.png)
   ![Image text](image/Snipaste_2022-03-01_14-58-19.png)
   ![Image text](image/Snipaste_2022-03-01_14-58-32.png)
   ![Image text](image/Snipaste_2022-03-01_14-59-51.png)
   ![Image text](image/Snipaste_2022-03-01_15-02-44.png)
   ![Image text](image/Snipaste_2022-03-01_15-04-44.png)
   

#### 参与贡献

本人比较菜，不喜勿喷  
网上参考的代码挺多的，谢谢前人的轮子。

大部分参考keke-cloud-ui

