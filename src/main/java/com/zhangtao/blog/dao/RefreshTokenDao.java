package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RefreshTokenDao extends JpaRepository<RefreshToken, String>, JpaSpecificationExecutor<RefreshToken> {

    RefreshToken findOneByTokenKey(String tokenKey);

    int deleteByUserId(String userId);

    void deleteAllByTokenKey(String tokenKey);
}
