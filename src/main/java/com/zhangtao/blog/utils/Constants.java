package com.zhangtao.blog.utils;

public interface Constants {

    int DEFAULT_SIZE = 30;

    /**
     * 单位：秒
     */
    interface TimeValueInSecond {

        int MINUTE = 60;
        int HOUR = 60 * MINUTE;
        int HOUR_2 = HOUR * 2;
        int DAY = HOUR * 24;
        int WEEK = DAY * 7;
        int MONTH = DAY * 30;
    }

    /**
     * 单位：豪秒
     */
    interface TimeValueInMillions{
        long SECOND = 1000;
        long MINUTE = 60 * SECOND;
        long HOUR = 60 * MINUTE;
        long HOUR_2 = HOUR * 2;
        long DAY = HOUR * 24;
        long WEEK = DAY * 7;
        long MONTH = DAY * 30;
    }

    interface ImageType{
        String PREFIX = "image/";
        String TYPE_JPG = "jpg";
        String TYPE_PNG = "png";
        String TYPE_GIF = "gif";
        String TYPE_JPG_WHIT_PREFIX = PREFIX + TYPE_JPG;
        String TYPE_PNG_WHIT_PREFIX = PREFIX + TYPE_PNG;
        String TYPE_GIF_WHIT_PREFIX = PREFIX + TYPE_GIF;
    }

    interface User{
        String ROLE_ADMIN = "roleAdmin";
        String ROLE_NORMAL = "roleNormal";
        String DEFAULT_AVATAR = "https://imgs.sunofbeaches.com/group1/M00/00/0E/rBsADV4c3lOAe3XQAAARPTB-zes803.png";
        String DEFAULT_STATE = "1";
        String KEY_REDIS_CAPTCHA = "key_redis_captcha_";
        String KEY_EMAIL_SEND_IP = "key_email_send_ip_";
        String KEY_EMAIL_CODE_CONTENT = "key_email_code_content_";
        String KEY_EMAIL_SEND_ADDRESS = "key_email_send_address_";
        String KEY_TOKEN = "key_token_";
        String COOKICE_TOKEN_EKY = "cookice_token_eky";
    }

    interface Settings {
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
        String WEBSITE_TITLE = "website_title";
        String WEBSITE_KEYWORDS = "website_keywords";
        String WEBSITE_DESC = "website_desc";
        String WEBSITE_VIEW_COUNT = "website_view_count";
    }

    interface Page {
        int DEFAULT_PAGE = 1;
        int MINI_PAGE_SIZE = 10;
    }

    interface Article {
        String STATE_DRAFT = "1";
        String STATE_PUBLISH = "2";
        int TITLE_MAX_LENGTH = 128;
        int SUMMARY_MAC_LENGTH = 256;
    }
}
