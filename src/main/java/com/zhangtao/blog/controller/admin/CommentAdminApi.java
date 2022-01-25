package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心评论的api
 */
@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {

    @Autowired
    private ICommentService commentService;

    /**
     * 删除评论
     *
     * @param commentId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId) {
        return commentService.deleteCommentById(commentId);
    }


    /**
     * 获取评论列表
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listComment(@PathVariable("page") int page, @PathVariable("size") int size) {
        return commentService.listComments(page, size);
    }

    /**
     * 置顶评论
     *
     * @param commentId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId") String commentId) {
        return commentService.topComment(commentId);
    }

    /**
     * 获取评论总数
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/count")
    public  ResponseResult getCommentCount(){
        return commentService.getCommentCount();
    }
}
