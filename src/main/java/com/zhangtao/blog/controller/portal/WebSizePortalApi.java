package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/web_size")
public class WebSizePortalApi {

    /**
     *获取文章分类
     *
     * @return
     */
    @GetMapping("/categorise")
    public ResponseResult getCategorise(){
        return null;
    }

    /**
     *获取网站标题
     *
     * @return
     */
    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return null;
    }

    /**
     *获取网站浏览量
     *
     * @return
     */
    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount(){
        return null;
    }

    /**
     *获取网站seo
     *
     * @return
     */
    @GetMapping("/seo")
    public ResponseResult getWebSizeViewSEO(){
        return null;
    }

    /**
     *获取网站轮播图
     *
     * @return
     */
    @GetMapping("/looper")
    public ResponseResult getWebSizeLooper(){
        return null;
    }

    /**
     *获取网站友情链接
     *
     * @return
     */
    @GetMapping("/friend_links")
    public ResponseResult getFriendLinks(){
        return null;
    }
}
