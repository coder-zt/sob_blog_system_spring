package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentDao extends JpaRepository<Comment, String>, JpaSpecificationExecutor<Comment> {
    /**
     * 通过Id查找评论
     *
     * @param commentId
     * @return
     */
    Comment findOneById(String commentId);

    int deleteAllByArticleId(String articleId);
}
