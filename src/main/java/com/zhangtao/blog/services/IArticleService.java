package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.responese.ResponseResult;

public interface IArticleService {

    ResponseResult postArticle(Article article);

    ResponseResult listArticle(int page, int size, String state, String keyword, String categoryId);

    ResponseResult getArticleById(String articleId);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult deleteArticle(String articleId);

    ResponseResult deleteArticleByState(String articleId);

    ResponseResult topArticle(String articleId);

    ResponseResult getTopArticle();

    ResponseResult listRecommendArticles(String articleId, int size);

    ResponseResult listArticle(int page, int size, String label);

    ResponseResult listLabels(int size);

    ResponseResult getArticleCount();
}