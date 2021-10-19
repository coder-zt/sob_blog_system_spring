package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.pojo.ArticleNoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleNoContentDao extends JpaRepository<ArticleNoContent, String>, JpaSpecificationExecutor<ArticleNoContent> {

    /**
     * 通过ID查找文章数据
     * 
     * @param articleId
     * @return
     */
    ArticleNoContent findOneById(String articleId);

    /**
     * 根据标签获取相似标签的文章
     * @param label
     * @param originArticleId
     * @param size
     * @return
     */
    @Query(nativeQuery = true, value = "select * from `tb_article` where `labels` like ? and 'id' != ? and (`state` = '1' or `state` = '3') limit ?")
    List<ArticleNoContent> listArticleByLikeLabel(String label, String originArticleId, int size);

    /**
     * 获取最新的文章
     * @param articleId
     * @param dxSize
     * @return
     */
    @Query(nativeQuery = true, value = "select * from `tb_article` where 'id' != ? and (`state` = '1' or `state` = '3') order by `create_time` desc limit ?")
    List<ArticleNoContent> listLastedArticleBySize(String articleId, int dxSize);
}