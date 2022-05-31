package com.keke.cloud.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keke.cloud.common.domain.RestResult;
import com.keke.cloud.web.domain.UserBean;

/**
 * @auther flk
 * @create 2021/2/23
 */
public interface IUserService extends IService<UserBean> {
    /*
    * 根据token获取用户
    * */
    UserBean getUserBeanByToken(String token);

    UserBean selectUserByopenid(String openid);

    /**
     * 用户注册
     *
     * @param userBean 用户信息
     * @return 结果
     */
    RestResult<String> registerUser(UserBean userBean);


    /*
    * 根据手机号查询用户
    * */
    UserBean findUserInfoByTelephone(String telephone);
}

