package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IWebsiteInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心网站信息的api
 */
@RestController
@RequestMapping("/admin/web_size_info")
public class WebsiteInfoApi {

    @Autowired
    private IWebsiteInfoService WebsiteInfoService;

    @GetMapping("/title")
    public ResponseResult getWebsiteTitle() {
        return WebsiteInfoService.getWebsiteTitle();
    }

    @PutMapping("/title")
    public ResponseResult updateWebsiteTitle(@RequestParam("title") String title) {
        return WebsiteInfoService.updateWebSiteTitle(title);
    }

    @GetMapping("/seo")
    public ResponseResult getSeoInfo() {
        return WebsiteInfoService.getSeoInfo();
    }

    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords,
            @RequestParam("description") String description) {
        return WebsiteInfoService.putSeoInfo(keywords, description);
    }

    @GetMapping
    public ResponseResult getWebsiteViewCount() {
        return WebsiteInfoService.getWebsiteViewCount();
    }
}
