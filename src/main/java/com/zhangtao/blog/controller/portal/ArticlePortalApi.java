package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    /**
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page")int page, @PathVariable("size")int size){
        return null;
    }

    /**
     * @return
     */
    @GetMapping("/list/{categoryID}/{page}/{size}")
    public ResponseResult listArticleById(@PathVariable("categoryId")int categoryId, @PathVariable("page")int page, @PathVariable("size")int size){
        return null;
    }
    /**
     * @return
     */
    @GetMapping("/{articleId}}")
    public ResponseResult getArticleDetail(@PathVariable("articleId")String articleId){
        return null;
    }

    /**
     * @return
     */
    @GetMapping("/recommend/{articleId}}")
    public ResponseResult getRecommendArticles(@PathVariable("articleId")String articleId){
        return null;
    }

}
