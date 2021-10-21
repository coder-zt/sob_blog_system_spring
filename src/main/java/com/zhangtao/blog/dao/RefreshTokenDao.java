package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenDao extends JpaRepository<RefreshToken, String>, JpaSpecificationExecutor<RefreshToken> {

    RefreshToken findOneByTokenKey(String tokenKey);

    RefreshToken findOneByMobileTokenKey(String tokenKey);

    int deleteByUserId(String userId);

    void deleteAllByTokenKey(String tokenKey);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_resfresh_token` set `mobile_token_key` = '' where `mobile_token_key= ?`")
    void deleteMobileTokenKey(String tokenKey);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_resfresh_token` set `token_key` = '' where `token_key= ?`")
    void deletePcTokenKey(String tokenKey);

    RefreshToken findOneByUserId(String userId);
}
