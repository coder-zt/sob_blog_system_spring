package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.responese.ResponseResult;

public interface IArticleService {

    ResponseResult postArticle(Article article);

    ResponseResult listArticle(int page, int size, String state, String keyword, String categoryId);

    ResponseResult getArticle(String articleId);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult deleteArticle(String articleId);

    ResponseResult deleteArticleByState(String articleId);

    ResponseResult topArticle(String articleId);
}