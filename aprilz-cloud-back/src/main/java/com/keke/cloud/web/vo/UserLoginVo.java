package com.keke.cloud.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "用户登录Vo")
public class UserLoginVo {
    @ApiModelProperty(value = "用户id", example = "1")
    private long userId;
    @ApiModelProperty(value = "用户名", example = "网盘")
    private String username;
    @ApiModelProperty(value = "真实名", example = "张三")
    private String realname;
    @ApiModelProperty(value = "qq用户名", example = "水晶之恋")
    private String qqUsername;
    @ApiModelProperty(value = "qq用户头像", example = "https://thirdqq.qlogo.cn/g?b=oidb&k=qxLE4dibR9sic8kS7mHLxlLw&s=100&t=1557468980")
    private String qqImageUrl;
    @ApiModelProperty(value = "手机号", example = "187****1817")
    private String telephone;
    @ApiModelProperty(value = "邮箱", example = "116****483@qq.com")
    private String email;
    @ApiModelProperty(value = "性别", example = "男")
    private String sex;
    @ApiModelProperty(value = "生日", example = "1994-05-06")
    private String birthday;
    @ApiModelProperty(value = "省", example = "陕西省")
    private String addrProvince;
    @ApiModelProperty(value = "市", example = "西安市")
    private String addrCity;
    @ApiModelProperty(value = "区", example = "雁塔区")
    private String addrArea;
    @ApiModelProperty(value = "行业", example = "计算机行业")
    private String industry;
    @ApiModelProperty(value = "职位", example = "java开发")
    private String position;
    @ApiModelProperty(value = "个人介绍", example = "错把陈醋当成墨，写尽半生都是酸")
    private String intro;
    @ApiModelProperty(value = "用户头像地址", example = "\\upload\\20200405\\93811586079860974.png")
    private String imageUrl;
    @ApiModelProperty(value = "注册时间", example = "2019-12-23 14:21:52")
    private String registerTime;
    @ApiModelProperty(value = "最后登录时间", example = "2019-12-23 14:21:52")
    private String lastLoginTime;
    @ApiModelProperty(value = "Token 接口访问凭证")
    private String token;

}
