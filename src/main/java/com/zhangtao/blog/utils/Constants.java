package com.zhangtao.blog.utils;

public interface Constants {

    int DEFAULT_SIZE = 30;
    interface User{
        String ROLE_ADMIN = "roleAdmin";
        String DEFAULT_AVATAR = "roleAdmin";
        String DEFAULT_STATE = "1";
        String KEY_REDIS_CAPTCHA = "key_redis_captcha_";
    }

    interface Settings{
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
    }
}
