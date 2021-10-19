package com.zhangtao.blog.services;

import com.zhangtao.blog.responese.ResponseResult;

public interface IWebsiteInfoService {

    ResponseResult getWebsiteTitle();

    ResponseResult updateWebSiteTitle(String title);

    ResponseResult getSeoInfo();

    ResponseResult putSeoInfo(String keywords, String description);

    ResponseResult getWebsiteViewCount();

    void updateViewCount();

}