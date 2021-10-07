package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request);

    void createCaptcha(HttpServletResponse response, String captchaKey)  throws Exception;
}
