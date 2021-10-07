package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.SobUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<SobUser, String>, JpaSpecificationExecutor<SobUser> {
}