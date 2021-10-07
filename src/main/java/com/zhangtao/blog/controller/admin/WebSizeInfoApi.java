package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心网站信息的api
 */
@RestController
@RequestMapping("/admin/web_size_info")
public class WebSizeInfoApi {

    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return null;
    }

    @PutMapping("/title")
    public ResponseResult updateWebSizeTitle(@RequestParam("title")String title){
        return null;
    }

    @GetMapping("/seo")
    public ResponseResult getSeoInfo(){
        return null;
    }

    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords, @RequestParam("description")String description){
        return null;
    }

    @GetMapping
    public  ResponseResult getWebSizeViewCount(){
        return null;
    }
}
