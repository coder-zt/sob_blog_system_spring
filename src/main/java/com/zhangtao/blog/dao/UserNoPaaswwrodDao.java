package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.pojo.SobUserNoPassword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface UserNoPaaswwrodDao extends JpaRepository<SobUserNoPassword, String>, JpaSpecificationExecutor<SobUserNoPassword> {

    /**
     * 根据用户名查找用户信息
     * @param userName
     * @return
     */
    SobUserNoPassword findOneByUserName(String userName);

    /**
     * 根据邮箱地址查找用户信息
     * @param email
     * @return
     */
    SobUserNoPassword findOneByEmail(String email);

    /**
     * 根据用户id查找用户信息
     *
     *
     * @param userId
     * @return
     */
    SobUserNoPassword findOneById(String userId);

    /**
     * 根据用户id删除禁用用户权限
     * @param userId
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET `state` = `0` WHERE `id` = ?")
    int deleteUserById(String userId);



    /**
     * 根据email更新用户密码
     * @param password
     * @param email
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET `password` = ? WHERE `email` = ?;")
    int updatePasswordByEmail(String password, String email);

    /**
     * 根据用户id更改邮箱地址
     *
     * @param email
     * @param id
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET `email` = ? WHERE `id` = ?;")
    int updateEmailById(String email, String id);
}
