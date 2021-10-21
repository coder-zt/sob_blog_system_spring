package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.interceptor.CheckTooFrequentCommit;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IWebsiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心网站信息的api
 */
@RestController
@RequestMapping("/admin/website_info")
public class WebSizeInfoAdminApi {

    @Autowired
    private IWebsiteInfoService websiteInfoService;

    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return websiteInfoService.getWebsiteTitle();
    }

    @CheckTooFrequentCommit
    @PutMapping("/title")
    public ResponseResult updateWebSizeTitle(@RequestParam("title")String title){
        return websiteInfoService.updateWebSiteTitle(title);
    }

    @GetMapping("/seo")
    public ResponseResult getSeoInfo(){
        return websiteInfoService.getSeoInfo();
    }

    @CheckTooFrequentCommit
    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords, @RequestParam("description")String description){
        return websiteInfoService.putSeoInfo(keywords,description);
    }

    @GetMapping
    public  ResponseResult getWebSizeViewCount(){
        return websiteInfoService.getWebsiteViewCount();
    }
}
