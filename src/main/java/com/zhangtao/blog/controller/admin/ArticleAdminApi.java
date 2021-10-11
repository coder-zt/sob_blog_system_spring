package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/admin/article")
public class ArticleAdminApi {

    /**
     * 添加分类
     *
     * @param article
     * @return
     */
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article){
        return null;
    }

    /**
     * 删除分类
     *
     * @param iamgeId
     * @return
     */
    @DeleteMapping("/{iamgeId}")
    public ResponseResult deleteArticle(@PathVariable("iamgeId") String iamgeId){
        return null;
    }

    /**
     * 修改分类
     *
     * @param iamgeId
     * @return
     */
    @PutMapping("/{iamgeId}")
    public ResponseResult updateArticle(@PathVariable("iamgeId") String iamgeId, @RequestBody Article article){
        return null;
    }

    /**
     * 获取分类
     *
     * @param iamgeId
     * @return
     */
    @GetMapping("/{iamgeId}")
    public ResponseResult getArticle(@PathVariable("iamgeId") String iamgeId){
        return null;
    }

    /**
     * 获取分类列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult addArticle(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }
}
