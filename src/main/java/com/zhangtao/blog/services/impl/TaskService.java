package com.zhangtao.blog.services.impl;

import com.zhangtao.blog.utils.EmailSender;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    public void sendEmailVerifyCode(String verifyCode, String emailAddress) throws Exception{
        EmailSender.sendRegisterVerifyCode(verifyCode, emailAddress);
    }
}
