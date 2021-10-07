package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/search")
public class SearchPortalApi {

    /**
     *获取文章分类
     *
     * @return
     */
    @GetMapping("/categorise")
    public ResponseResult doSearch(@RequestParam("keywords") String keywords, @RequestParam("page")int page, @RequestParam("size") int size){
        return null;
    }


}
