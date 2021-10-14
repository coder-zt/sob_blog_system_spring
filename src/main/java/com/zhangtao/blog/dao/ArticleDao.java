package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleDao extends JpaRepository<Article, String>, JpaSpecificationExecutor<Article> {

    /**
     * 通过ID查找文章数据
     * 
     * @param articleId
     * @return
     */
    Article findOneById(String articleId);

}