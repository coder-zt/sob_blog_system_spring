package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser);

    void createCaptcha(String captchaKey) throws Exception;

    ResponseResult sendEmail(String type, String emailAddress);

    ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey);

    ResponseResult doLogin(SobUser sobUser, String captchaKey, String captcha);

    SobUser checkSobUser();

    ResponseResult getUserInfo(String userId);

    ResponseResult checkEmail(String email);

    ResponseResult checkUserName(String userName);

    ResponseResult updateUserInfo(String userId, SobUser sobUser);

    ResponseResult deleteUser(String userId);

    ResponseResult listUsers(int page, int size);

    ResponseResult updatePassword(String verifyCode, SobUser sobUser);

    ResponseResult updateEmail(String email, String verifyCode);

    ResponseResult doLogout();
}
