package com.zhangtao.blog.services.impl;

import com.zhangtao.blog.dao.FriendLinkDao;
import com.zhangtao.blog.pojo.FriendLink;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IFriendLinkService;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class FriendLinkServiceImpl extends BaseService implements IFriendLinkService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private FriendLinkDao friendLinkDao;

    @Autowired
    private IUserService userService;

    @Override
    public ResponseResult addFriendLink(FriendLink friendFlink) {
        //判断数据
        // 链接、封面、名称
        if(TextUtils.isEmpty(friendFlink.getLogo())){
            return ResponseResult.FAILED("友情链接封面不可以为空");
        }
        if(TextUtils.isEmpty(friendFlink.getName())){
            return ResponseResult.FAILED("友情链接名称不可以为空");
        }
        if(TextUtils.isEmpty(friendFlink.getUrl())){
            return ResponseResult.FAILED("友情链接不可以为空");
        }
        //补充数据
        friendFlink.setId(idWorker.nextId() + "");
        friendFlink.setState("1");
        friendFlink.setCreateTime(new Date());
        friendFlink.setUpdateTime(new Date());
        //保存数据
        friendLinkDao.save(friendFlink);
        //返回结果
        return ResponseResult.SUCCESS("新增友情链接成功！");
    }

    @Override
    public ResponseResult getFriendLink(String friendLinkId) {
        //获取数据
        FriendLink friendLinkFromDb = friendLinkDao.findOneById(friendLinkId);
        if (friendLinkFromDb == null) {
            return ResponseResult.FAILED("该友情链接不存在");
        }
        //返回数据
        return ResponseResult.SUCCESS("获取友情链接成功").setData(friendLinkFromDb);
    }

    @Override
    public ResponseResult listFriendLinks() {
        //创建查询条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "order");
        SobUser sobUser = userService.checkSobUser();
        List<FriendLink> all;
        if(sobUser == null || !Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())){
            all = friendLinkDao.listFriendLinkByState("1");
        }else{
            all = friendLinkDao.findAll(sort);
        }
        //查询
        //返回结果
        return ResponseResult.SUCCESS("获取链接列表成功").setData(all);
    }

    @Override
    public ResponseResult deleteFriendLink(String friendLinkId) {
        int result = friendLinkDao.deleteById(friendLinkId);
        return getDeleteResult(result, "友情链接");
    }

    @Override
    public ResponseResult updateFriendLink(String friendLinkId, FriendLink friendFlink) {
        //判断修改对象是否存在
        FriendLink friendLinkFromDb = friendLinkDao.findOneById(friendLinkId);
        if (friendLinkFromDb == null) {
            return  ResponseResult.FAILED("更新友情链接不存在");
        }
        //判断数据
        // 链接、封面、名称
        String logo = friendFlink.getLogo();
        if(!TextUtils.isEmpty(logo)){
            friendLinkFromDb.setLogo(logo);
        }
        String name = friendFlink.getName();
        if(!TextUtils.isEmpty(name)){
            friendLinkFromDb.setName(name);
        }
        String url = friendFlink.getUrl();
        if(!TextUtils.isEmpty(url)){
            friendLinkFromDb.setUrl(url);
        }
        String state = friendFlink.getState();
        if(!TextUtils.isEmpty(state)){
            friendLinkFromDb.setState(state);
        }
        //保存数据
        friendLinkDao.save(friendLinkFromDb);
        //返回结果
        return ResponseResult.SUCCESS("友情链接更新成功");
    }
}
