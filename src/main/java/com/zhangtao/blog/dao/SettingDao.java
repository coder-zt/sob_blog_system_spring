package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SettingDao extends JpaRepository<Settings, String>, JpaSpecificationExecutor<Settings> {

    /**
     * 通过key获取属性值
     * 
     * @param websiteTitle
     * @return
     */
    Settings findOneByKey(String websiteTitle);

}