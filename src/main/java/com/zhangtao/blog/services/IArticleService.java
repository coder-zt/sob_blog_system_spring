package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.responese.ResponseResult;

public interface IArticleService {

    ResponseResult postArticle(Article article);

}