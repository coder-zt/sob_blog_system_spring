package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentDao extends JpaRepository<Comment, String>, JpaSpecificationExecutor<Comment> {
    /**
     * 通过Id查找评论
     *
     * @param commentId
     * @return
     */
    Comment findOneById(String commentId);

    int deleteAllByArticleId(String articleId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_comment` set `state` = 0 where `id` = ?")
    int deleteByUpdateState(String loopId);

    Page<Comment> findAllByArticleId(String articleId, Pageable pageable);
}
