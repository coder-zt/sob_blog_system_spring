package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request);

    void createCaptcha(HttpServletResponse response, String captchaKey)  throws Exception;

    ResponseResult sendEmail(HttpServletRequest request,String type,  String emailAddress);

    ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request);
}
