package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ICategoryService;
import com.zhangtao.blog.services.IFriendLinkService;
import com.zhangtao.blog.services.ILooperService;
import com.zhangtao.blog.services.IWebsiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/web_size")
public class WebSizePortalApi {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IFriendLinkService friendLinkService;

    @Autowired
    private ILooperService looperService;

    @Autowired
    private IWebsiteInfoService websiteInfoService;

    /**
     *获取文章分类
     *
     * @return
     */
    @GetMapping("/categorise")
    public ResponseResult getCategorise(){
        return categoryService.listCategories();
    }

    /**
     *获取网站标题
     *
     * @return
     */
    @GetMapping("/title")
    public ResponseResult getWebsiteTitle(){
        return websiteInfoService.getWebsiteTitle();
    }

    /**
     *获取网站浏览量
     *
     * @return
     */
    @GetMapping("/view_count")
    public ResponseResult getWebsiteViewCount(){
        return websiteInfoService.getWebsiteViewCount();
    }

    /**
     *获取网站seo
     *
     * @return
     */
    @GetMapping("/seo")
    public ResponseResult getWebsiteViewSEO(){
        return websiteInfoService.getSeoInfo();
    }

    /**
     *获取网站轮播图
     *
     * @return
     */
    @GetMapping("/looper")
    public ResponseResult getWebsiteLooper(){
        return looperService.listLoop();
    }

    /**
     *获取网站友情链接
     *
     * @return
     */
    @GetMapping("/friend_links")
    public ResponseResult getFriendLinks(){
        return friendLinkService.listFriendLinks();
    }

    @PutMapping("/view_count")
    public void updateViewCount(){
        websiteInfoService.updateViewCount();
    }
}
