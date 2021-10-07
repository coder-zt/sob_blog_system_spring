package com.zhangtao.blog.utils;

public interface Constants {

    int DEFAULT_SIZE = 30;
    interface User{
        String ROLE_ADMIN = "roleAdmin";
        String DEFAULT_AVATAR = "roleAdmin";
        String DEFAULT_STATE = "1";
        String KEY_REDIS_CAPTCHA = "key_redis_captcha_";
        String KEY_EMAIL_SEND_IP = "key_email_send_ip_";
        String KEY_EMAIL_CODE_CONTENT = "key_email_code_content_";
        String KEY_EMAIL_SEND_ADDRESS = "key_email_send_address_";
    }

    interface Settings{
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
    }
}
