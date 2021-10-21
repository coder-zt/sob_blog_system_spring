package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.interceptor.CheckTooFrequentCommit;
import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.adminPermission()")
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article) {
        return articleService.postArticle(article);
    }

    /**
     * 删除分类
     *
     * @param articleId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{articleId}")
    public ResponseResult deleteArticle(@PathVariable("articleId") String articleId) {
        return articleService.deleteArticle(articleId);
    }

    /**
     * 修改文章
     *
     * @param articleId
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.adminPermission()")
    @PutMapping("/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId") String articleId, @RequestBody Article article) {
        return articleService.updateArticle(articleId,article);
    }

    /**
     * 获取文章详情
     *
     * @param articleId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/{articleId}")
    public ResponseResult getArticle(@PathVariable("articleId") String articleId) {
        return articleService.getArticleById(articleId);
    }

    /**
     * 获取分类列表
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page") int page,
                                        @PathVariable("size")int size,
                                        @RequestParam(value = "state", required = false) String state,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "categoryId", required = false) String categoryId) {
        return articleService.listArticle(page, size,state, keyword, categoryId);
    }

    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/state/{articleId}")
    public ResponseResult deleteArticleByState(@PathVariable("articleId") String articleId) {
        return articleService.deleteArticleByState(articleId);
    }

    @PreAuthorize("@permission.adminPermission()")
    @PutMapping("/top/{articleId}")
    public ResponseResult topArticle(@PathVariable("articleId") String articleId) {

        return articleService.topArticle(articleId);
    }
}
