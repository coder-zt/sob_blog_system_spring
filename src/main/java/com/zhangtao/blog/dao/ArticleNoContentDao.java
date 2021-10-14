package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.pojo.ArticleNoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleNoContentDao extends JpaRepository<ArticleNoContent, String>, JpaSpecificationExecutor<ArticleNoContent> {

    /**
     * 通过ID查找文章数据
     * 
     * @param articleId
     * @return
     */
    ArticleNoContent findOneById(String articleId);

}