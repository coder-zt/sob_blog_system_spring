package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IArticleService;
import com.zhangtao.blog.services.ICategoryService;
import com.zhangtao.blog.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ICategoryService categoryService;

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
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page")int page, @PathVariable("size")int size){
        return articleService.listArticle(page, size, Constants.Article.STATE_PUBLISH, null, null);
    }

    /**
     * @return
     */
    @GetMapping("/list/{categoryId}/{page}/{size}")
    public ResponseResult listArticleByCategoryId(@PathVariable("categoryId")String categoryId,
                                          @PathVariable("page")int page,
                                          @PathVariable("size")int size){
        return articleService.listArticle(page, size, Constants.Article.STATE_PUBLISH, null, categoryId);
    }
    /**
     * 获取文章详情
     *
     * @return
     */
    @GetMapping("/{articleId}}")
    public ResponseResult getArticleDetail(@PathVariable("articleId")String articleId){
        return articleService.getArticleById(articleId);
    }

    /**
     *
     * 获取推荐文章
     *
     * @return
     */
    @GetMapping("/recommend/{articleId}/{size}")
    public ResponseResult getRecommendArticles(@PathVariable("articleId")String articleId, @PathVariable("size")int size){
        return articleService.listRecommendArticles(articleId, size);
    }

    /**
     * 获取置顶文章
     * @return
     */
    @GetMapping("/top")
    public ResponseResult getTopArticle(){
        return articleService.getTopArticle();
    }


    /**
     * @return
     */
    @GetMapping("/list/label/{label}/{page}/{size}")
    public ResponseResult listArticleByLabel(@PathVariable("label")String label, @PathVariable("page")int page, @PathVariable("size")int size){
        return articleService.listArticle(page, size,label);
    }

    /**
     * 获取标签云
     * @param size
     * @return
     */
    @GetMapping("/label/{size}")
    public ResponseResult getLabels(@PathVariable("size") int size){
        return articleService.listLabels(size);
    }
}
