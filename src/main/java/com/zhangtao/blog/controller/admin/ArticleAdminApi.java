package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心文章的api
 */
@RestController
@RequestMapping("/admin/article")
public class ArticleAdminApi {

    @Autowired
    private IArticleService articleService;

    /**
     * 发表文章
     *
     * @param article
     * @return
     */
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article) {
        return articleService.postArticle(article);
    }

    /**
     * 删除分类
     *
     * @param iamgeId
     * @return
     */
    @DeleteMapping("/{iamgeId}")
    public ResponseResult deleteArticle(@PathVariable("iamgeId") String iamgeId) {
        return null;
    }

    /**
     * 修改分类
     *
     * @param iamgeId
     * @return
     */
    @PutMapping("/{iamgeId}")
    public ResponseResult updateArticle(@PathVariable("iamgeId") String iamgeId, @RequestBody Article article) {
        return null;
    }

    /**
     * 获取分类
     *
     * @param iamgeId
     * @return
     */
    @GetMapping("/{iamgeId}")
    public ResponseResult getArticle(@PathVariable("iamgeId") String iamgeId) {
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
    public ResponseResult addArticle(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }

    @PutMapping("/state/{articleId}/{state}")
    public ResponseResult updateArticleState(@PathVariable("articleId") String articleId,
            @PathVariable("state") String state) {

        return null;
    }

    @PutMapping("/top/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId") String articleId) {

        return null;
    }
}
