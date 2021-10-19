package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.responese.ResponseResult;

public interface ISolrService {

    ResponseResult doSearch(String keywords, int page, int size, String categoryId, Integer sort);

    void addArticle(Article article);

    void deleteArticle(String articleId);


    void updateArticle(String articleId, Article article);
}
