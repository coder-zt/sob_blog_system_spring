package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageDao extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {


    @Modifying
    @Query(nativeQuery = true, value = "update `tb_images` set `state` = 1 where `id` = ?;")
    int deleteImageByUpdateState(String imageId);
}
