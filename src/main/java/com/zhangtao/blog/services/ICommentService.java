package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.responese.ResponseResult;


public interface ICommentService {
    ResponseResult postComment(Comment comment);

    ResponseResult listArticleComments(String articleId, int page, int size);

    ResponseResult deleteCommentById(String commentId);

    ResponseResult listComments(int page, int size);

    ResponseResult topComment(String commentId);
}
