package com.zhangtao.blog.services.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zhangtao.blog.dao.SettingDao;
import com.zhangtao.blog.pojo.Settings;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IWebsiteInfoService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.TextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WebsiteInfoService implements IWebsiteInfoService {

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private IdWorker IdWorker;

    @Override
    public ResponseResult getWebsiteTitle() {
        Settings settingFromDb = settingDao.findOneByKey(Constants.Settings.WEBSITE_TITLE);
        if (settingFromDb == null) {
            return ResponseResult.FAILED("还没有给网站设置标题，请先设置.");
        }
        return ResponseResult.SUCCESS("获取网站标题成功.").setData(settingFromDb.getValue());
    }

    @Override
    public ResponseResult updateWebSiteTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("网站标题不可以为空.");
        }
        Settings settingFromDb = settingDao.findOneByKey(Constants.Settings.WEBSITE_TITLE);
        if (settingFromDb == null) {
            settingFromDb = new Settings();
            settingFromDb.setKey(Constants.Settings.WEBSITE_TITLE);
            settingFromDb.setId(IdWorker.nextId() + "");
            settingFromDb.setCreateTime(new Date());
        }
        settingFromDb.setUpdateTime(new Date());
        settingFromDb.setValue(title);
        settingDao.save(settingFromDb);
        return ResponseResult.SUCCESS("网站标题设置成功.");
    }

    @Override
    public ResponseResult getSeoInfo() {
        Settings keywordsFromDb = settingDao.findOneByKey(Constants.Settings.WEBSITE_KEYWORDS);
        Settings descFromDb = settingDao.findOneByKey(Constants.Settings.WEBSITE_DESC);
        if (keywordsFromDb == null || descFromDb == null) {
            return ResponseResult.FAILED("SEO信息未设置.");
        }
        Map<String, String> result = new HashMap<>();
        result.put(keywordsFromDb.getKey(), keywordsFromDb.getValue());
        result.put(descFromDb.getKey(), descFromDb.getValue());
        return ResponseResult.SUCCESS("获取seo信息成功.").setData(result);
    }

    @Override
    public ResponseResult putSeoInfo(String keywords, String description) {
        if (TextUtils.isEmpty(keywords)) {
            return ResponseResult.FAILED("关键字不可以为空.");
        }
        if (TextUtils.isEmpty(description)) {
            return ResponseResult.FAILED("描述不可以为空.");
        }
        Settings keywordsFromDb = settingDao.findOneByKey(Constants.Settings.WEBSITE_KEYWORDS);
        if (keywordsFromDb == null) {
            keywordsFromDb = new Settings();
            keywordsFromDb.setKey(Constants.Settings.WEBSITE_KEYWORDS);
            keywordsFromDb.setId(IdWorker.nextId() + "");
            keywordsFromDb.setCreateTime(new Date());
        }
        keywordsFromDb.setUpdateTime(new Date());
        keywordsFromDb.setValue(keywords);
        settingDao.save(keywordsFromDb);
        Settings descFromDb = settingDao.findOneByKey(Constants.Settings.WEBSITE_DESC);
        if (descFromDb == null) {
            descFromDb = new Settings();
            descFromDb.setKey(Constants.Settings.WEBSITE_DESC);
            descFromDb.setId(IdWorker.nextId() + "");
            descFromDb.setCreateTime(new Date());
        }
        descFromDb.setUpdateTime(new Date());
        descFromDb.setValue(description);
        settingDao.save(descFromDb);
        return ResponseResult.SUCCESS("SEO信息更新成功！");
    }

    /**
     * 文章的浏览量
     */
    @Override
    public ResponseResult getWebsiteViewCount() {
        Settings viewCountFromDb = settingDao.findOneByKey(Constants.Settings.WEBSITE_VIEW_COUNT);
        if (viewCountFromDb == null) {
            viewCountFromDb = new Settings();
            viewCountFromDb.setKey(Constants.Settings.WEBSITE_VIEW_COUNT);
            viewCountFromDb.setId(IdWorker.nextId() + "");
            viewCountFromDb.setCreateTime(new Date());
            viewCountFromDb.setUpdateTime(new Date());
            viewCountFromDb.setValue("1");
            settingDao.save(viewCountFromDb);
        }
        Map<String, String> result = new HashMap<>();
        result.put(viewCountFromDb.getKey(), viewCountFromDb.getValue());
        return ResponseResult.SUCCESS("网站浏览量查询成功").setData(result);
    }

}