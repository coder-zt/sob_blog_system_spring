package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {


    @Autowired
    private ICommentService commentService;

    /**
     * 发表评论
     *
     * @param comment
     * @return
     */
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment){
        return commentService.postComment(comment);
    }

    /**
     * 删除评论
     *
     * @return
     */
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId){
        return commentService.deleteCommentById(commentId);
    }

    /**
     * 获取文章评论
     *
     * @param articleId
     * @return
     */
    @GetMapping("/list/{articleId}/{page}/{size}")
    public ResponseResult listArticleComments(@PathVariable("articleId") String articleId,
                                              @PathVariable("page")int page, @PathVariable("size") int size){
        return commentService.listArticleComments(articleId, page, size);
    }

}
