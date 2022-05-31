package com.keke.cloud.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.keke.cloud.common.domain.RestResult;
import com.keke.cloud.common.dto.RegisterDTO;
import com.keke.cloud.common.util.JjwtUtil;
import com.keke.cloud.web.domain.UserBean;
import com.keke.cloud.web.service.IUserService;
import com.keke.cloud.web.vo.UserLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther flk
 * @create 2021/2/23
 */
@Api(value = "user", tags = "该接口为用户接口，主要做用户登录，注册和校验token")
@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Resource
    IUserService userService;



    public static Map<String, String> verificationCodeMap = new HashMap<>();

    public static final int TEXT = 4;

    /**
     * 当前模块
     */
    public static final String CURRENT_MODULE = "用户管理";


    @ApiOperation(value = "用户注册", notes = "注册账号", tags = {"user"})
    @PostMapping(value = "/register")
    @ResponseBody
    public RestResult<String> addUser(@RequestBody RegisterDTO registerDTO) {
        RestResult<String> restResult = null;
        UserBean userBean = new UserBean();
        //将dto属性值赋值到bean,切记属性名需要相同才能赋值，否则需要手动赋值。
        BeanUtil.copyProperties(registerDTO, userBean);
        restResult = userService.registerUser(userBean);

        return restResult;
    }

    @ApiOperation(value = "用户登录", notes = "用户登录认证后才能进入系统", tags = {"user"})
    @GetMapping("/login")
    @ResponseBody
    public RestResult<UserLoginVo> userLogin(
            @ApiParam(value = "登录用户名") String username,
            @ApiParam(value = "登录密码") String password) {
        RestResult<UserLoginVo> restResult = new RestResult<UserLoginVo>();
        UserBean saveUserBean = userService.findUserInfoByTelephone(username);//查询用户

        String jwt = "";
        try {
            //生成token
            jwt = JjwtUtil.createJWT("aprilzCloud", "aprilz", JSON.toJSONString(saveUserBean));
        } catch (Exception e) {
            log.info("登录失败：{}", e);
            restResult.setSuccess(false);
            restResult.setErrorMessage("登录失败！");
            return restResult;
        }

        String passwordHash = new SimpleHash("MD5", password, saveUserBean.getSalt(), 1024).toHex();
        //判断密码
        if (passwordHash.equals(saveUserBean.getPassword())) {
            UserLoginVo userLoginVo = new UserLoginVo();
            BeanUtil.copyProperties(saveUserBean, userLoginVo);
            userLoginVo.setToken(jwt);//签发token
            restResult.setData(userLoginVo);
            restResult.setSuccess(true);
        } else {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号或密码错误！");
        }
        return restResult;//响应
    }

    @ApiOperation(value = "检查用户登录信息", notes = "验证token的有效性", tags = {"user"})
    @GetMapping("/checkuserlogininfo")
    @ResponseBody
    public RestResult<UserBean> checkUserLoginInfo(@RequestHeader("token") String token) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        log.info("检查用户登录信息,token:"+token+",是否为空:"+StringUtils.isEmpty(token));
        if (token.equals("undefined") || StringUtils.isEmpty(token)){
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户暂未登录");
        }else{
            UserBean sessionUserBean = userService.getUserBeanByToken(token);//认证token
            if (sessionUserBean != null) {

                restResult.setData(sessionUserBean);
                restResult.setSuccess(true);
            } else {
                restResult.setSuccess(false);
                restResult.setErrorMessage("用户暂未登录");
            }
        }
        return restResult;
    }

}
