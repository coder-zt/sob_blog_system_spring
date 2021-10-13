package com.zhangtao.blog.services.impl;

import java.util.Date;

import com.zhangtao.blog.dao.LoopDao;
import com.zhangtao.blog.pojo.Looper;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ILooperService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.TextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LooperServiceImpl implements ILooperService {

    @Autowired
    private IdWorker IdWorker;

    @Autowired
    private LoopDao loopDao;

    @Override
    public ResponseResult addLooper(Looper looper) {
        // 检查数据
        String title = looper.getTitle();
        if (TextUtils.isEmpty(title)) {
            ResponseResult.FAILED("Loop的标题不可以为空！");
        }
        String imageUrl = looper.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            ResponseResult.FAILED("Loop的图片路径不可以为空！");
        }
        String targetUrl = looper.getTargetUrl();
        if (TextUtils.isEmpty(targetUrl)) {
            ResponseResult.FAILED("Loop的目标链接不可以为空！");
        }
        looper.setOrder(looper.getOrder());
        // 补充数据
        looper.setId(IdWorker.nextId() + "");
        looper.setState("1");
        looper.setcreateTime(new Date());
        looper.setupdateTime(new Date());
        // 保存数据
        loopDao.save(looper);
        // 返回结果
        return ResponseResult.SUCCESS("轮播图添加成功.");
    }

    @Override
    public ResponseResult listLoop(int page, int size) {
        // 参数检查
        if (page > Constants.Page.DEFAULT_PAGE) {
            page = Constants.Page.DEFAULT_PAGE;
        }
        if (size > Constants.Page.MINI_PAGE_SIZE) {
            size = Constants.Page.MINI_PAGE_SIZE;
        }
        // 创建查询条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        Page<Looper> all = loopDao.findAll(pageable);
        return ResponseResult.SUCCESS("查询数据成功！").setData(all);
    }

    @Override
    public ResponseResult getLoop(String loopId) {
        Looper loopFromDb = loopDao.findOneById(loopId);
        if (loopFromDb == null) {
            return ResponseResult.FAILED("该轮播图不存在");
        }
        return ResponseResult.SUCCESS("获取轮播图成功呢！").setData(loopFromDb);
    }

    @Override
    public ResponseResult updateLoop(String loopId, Looper loop) {
        // 找出来判空
        Looper loopFromDb = loopDao.findOneById(loopId);
        if (loopFromDb == null) {// 没有-
            return ResponseResult.FAILED("轮播图不存在！");
        }
        // 有->修改
        String imageUrl = loop.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            loopFromDb.setImageUrl(imageUrl);
        }
        String title = loop.getTitle();
        if (!TextUtils.isEmpty(title)) {
            loopFromDb.setTitle(title);
        }
        String targetUrl = loop.getTargetUrl();
        if (!TextUtils.isEmpty(targetUrl)) {
            loopFromDb.setTargetUrl(targetUrl);
        }
        String state = loopFromDb.getState();
        if (!TextUtils.isEmpty(state)) {
            loopFromDb.setState(state);
        }
        loopFromDb.setOrder(loop.getOrder());
        loopFromDb.setupdateTime(new Date());
        // 保存
        loopDao.save(loopFromDb);
        // 返回结果
        return ResponseResult.SUCCESS("轮播图更新成功！");
    }

    @Override
    public ResponseResult deleteLoop(String loopId) {
        int result = loopDao.deleteByUpdateSatate(loopId);
        return result > 0 ? ResponseResult.SUCCESS("轮播图删除成功!") : ResponseResult.FAILED("轮播图删除失败!");
    }

}