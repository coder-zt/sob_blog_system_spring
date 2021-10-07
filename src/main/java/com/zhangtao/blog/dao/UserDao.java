package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.SobUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<SobUser, String>, JpaSpecificationExecutor<SobUser> {

    /**
     * 根据用户名查找用户信息
     * @param userName
     * @return
     */
    SobUser findOneByUserName(String userName);

    /**
     * 根据邮箱地址查找用户信息
     * @param email
     * @return
     */
    SobUser findOneByEmail(String email);
}
