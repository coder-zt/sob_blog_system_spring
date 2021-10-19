package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ISolrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/search")
public class SearchPortalApi {


    @Autowired
    private ISolrService solrService;


    /**
     *获取文章分类
     *
     * @return
     */
    @GetMapping
    public ResponseResult doSearch(@RequestParam("keywords") String keywords,
                                   @RequestParam("page")int page,
                                   @RequestParam("size") int size,
                                   @RequestParam(value="categoryId", required = false) String categoryId,
                                   @RequestParam(value="sort", required = false) Integer sort){
        return solrService.doSearch(keywords, page, size, categoryId, sort);
    }


}
