package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.pojo.FriendLink;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IFriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心友情链接的api
 */
@RestController
@RequestMapping("/admin/friends")
public class FriendLinkAdminApi {


    @Autowired
    private IFriendLinkService friendLinkService;


    /**
     * 添加
     *
     * @param friendFlink
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @PostMapping
    public ResponseResult addFriends(@RequestBody FriendLink friendFlink){
        return friendLinkService.addFriendLink(friendFlink);
    }

    /**
     * 删除分类
     *
     * @param friendLinkId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{friendLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLinkId") String friendLinkId){
        return friendLinkService.deleteFriendLink(friendLinkId);
    }

    /**
     * 修改分类
     *
     * @param friendLinkId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @PutMapping("/{friendLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendLinkId") String friendLinkId, @RequestBody FriendLink friendFlink){
        return friendLinkService.updateFriendLink(friendLinkId, friendFlink);
    }

    /**
     * 获取友情链接
     *
     * @param friendLinkId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/{friendLinkId}")
    public ResponseResult getFriends(@PathVariable("friendLinkId") String friendLinkId){
        return friendLinkService.getFriendLink(friendLinkId);
    }

    /**
     * 获取分类列表
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listFriendLinks(@PathVariable("page") int page, @PathVariable("size") int size){
        return friendLinkService.listFriendLinks();
    }
}
