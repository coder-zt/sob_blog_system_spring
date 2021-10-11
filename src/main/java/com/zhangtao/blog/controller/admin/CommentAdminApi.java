package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {

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
     * 修改分类
     *
     * @param commentId
     * @return
     */
    @PutMapping("/{commentId}")
    public ResponseResult updateComment(@PathVariable("commentId") String commentId, @RequestBody Comment comment){
        return null;
    }



    /**
     * 获取分类列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult addComment(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }

    /**
     * 置顶评论
     *
     * @param commentId
     * @return
     */
    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId")String commentId) {
        return null;
    }
}
