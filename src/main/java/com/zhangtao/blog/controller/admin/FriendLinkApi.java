package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.pojo.FriendFlink;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/admin/friends")
public class FriendLinkApi {

    /**
     * 添加分类
     *
     * @param friendFlink
     * @return
     */
    @PostMapping
    public ResponseResult addFriends(@RequestBody FriendFlink friendFlink){
        return null;
    }

    /**
     * 删除分类
     *
     * @param friendsId
     * @return
     */
    @DeleteMapping("/{friendsId}")
    public ResponseResult deleteFriends(@PathVariable("friendsId") String friendsId){
        return null;
    }

    /**
     * 修改分类
     *
     * @param friendsId
     * @return
     */
    @PutMapping("/{friendsId}")
    public ResponseResult updateFriends(@PathVariable("friendsId") String friendsId, @RequestBody FriendFlink friendFlink){
        return null;
    }

    /**
     * 获取分类
     *
     * @param friendsId
     * @return
     */
    @GetMapping("/{friendsId}")
    public ResponseResult getFriends(@PathVariable("friendsId") String friendsId){
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
    public ResponseResult addFriends(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }
}
