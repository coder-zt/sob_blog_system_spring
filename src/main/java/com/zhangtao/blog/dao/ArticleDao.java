package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleDao extends JpaRepository<Article, String>, JpaSpecificationExecutor<Article> {

    /**
     * 通过ID查找文章数据
     * 
     * @param articleId
     * @return
     */
    Article findOneById(String articleId);

    int deleteById(String articleId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_article` set `state` = 0 where `id` = ?;")
    int deleteArticleByUpdateState(String articleId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_article` set `state` = 3 where `id` = ?;")
    int topArticle(String articleId);
}