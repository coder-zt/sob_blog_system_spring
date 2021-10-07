package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {

    /**
     * 添加分类
     *
     * @param comment
     * @return
     */
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment){
        return null;
    }

    /**
     * 删除分类
     *
     * @param commentId
     * @return
     */
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId){
        return null;
    }

    /**
     * 获取文章评论
     *
     * @param articleId
     * @return
     */
    @GetMapping("/list/{articleId}")
    public ResponseResult listArticleComments(@PathVariable("articleId") String articleId){
        return null;
    }

}
