package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendLinkDao extends JpaRepository<FriendLink, String>, JpaSpecificationExecutor<FriendLink> {

    /**
     * 根据id获取友情链接数据
     *
     *
     * @param friendLinkId
     * @return
     */
    FriendLink findOneById(String friendLinkId);

    /**
     * 逻辑删除友情链接
     *
     * @param friendLinkId
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "update `tb_friends` set `state` = 0 where `id` = ?;")
    int deleteFriendLinkByUpdateStatus(String friendLinkId);

    int deleteById(String friendLinkId);

    @Query(nativeQuery = true, value = "select * from `tb_friends` where `state` = ?;")
    List<FriendLink> listFriendLinkByState(String s);
}
