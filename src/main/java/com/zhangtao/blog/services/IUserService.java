package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser);

    void createCaptcha() throws Exception;

    ResponseResult sendEmail(String type, String emailAddress,String captchaCode);

    ResponseResult register(SobUser sobUser, String emailCode, String captchaCode);

    ResponseResult doLogin(SobUser sobUser, String key, String from);

    SobUser checkSobUser();

    ResponseResult getUserInfo(String userId);

    ResponseResult checkEmail(String email);

    ResponseResult checkUserName(String userName);

    ResponseResult updateUserInfo(String userId, SobUser sobUser);

    ResponseResult deleteUser(String userId);

    ResponseResult listUsers(int page, int size,String userName, String email);

    ResponseResult updatePassword(String verifyCode, SobUser sobUser);

    ResponseResult updateEmail(String email, String verifyCode);

    ResponseResult doLogout();

    ResponseResult getPcLoginQrCodeInfo();

    ResponseResult checkQrCodeLoginState(String loginId);

    ResponseResult parseToken();

    ResponseResult resetPassword(String userId, String password);

    ResponseResult getRegisterCount();

    ResponseResult checkEmailCode(String email, String emailCode, String captchaCode);
}
