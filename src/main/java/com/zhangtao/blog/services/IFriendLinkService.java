package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.FriendLink;
import com.zhangtao.blog.responese.ResponseResult;

public interface IFriendLinkService {

    ResponseResult addFriendLink(FriendLink friendFlink);

    ResponseResult getFriendLink(String friendLinkId);

    ResponseResult listFriendLinks();

    ResponseResult deleteFriendLink(String friendLinkId);

    ResponseResult updateFriendLink(String friendLinkId, FriendLink friendFlink);

}
