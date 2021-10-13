package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Looper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LoopDao extends JpaRepository<Looper, String>, JpaSpecificationExecutor<Looper> {

    /**
     * 根据id查找轮播图
     * 
     * @param looperId
     * @return
     */
    Looper findOneByLoopId(String looperId);

    /**
     * 逻辑删除轮播图
     * 
     * @param loopId
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "update `tb_looper` set `state` = `0` where `id` = ?")
    int deleteByUpdateSatate(String loopId);

}