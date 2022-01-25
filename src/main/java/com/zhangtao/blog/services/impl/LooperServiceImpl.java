package com.zhangtao.blog.services.impl;

import java.util.Date;
import java.util.List;

import com.zhangtao.blog.dao.LoopDao;
import com.zhangtao.blog.pojo.Looper;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ILooperService;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.TextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LooperServiceImpl extends BaseService implements ILooperService {

    @Autowired
    private IdWorker IdWorker;

    @Autowired
    private LoopDao loopDao;

    @Autowired
    private IUserService userService;

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
    public ResponseResult listLoop() {
        //创建查询条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "order");
        SobUser sobUser = userService.checkSobUser();
        //查询
        List<Looper> all;
        if(sobUser == null || !Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())){
            all = loopDao.listLoopByState("1");
        }else{
            all = loopDao.findAll(sort);
        }
        //返回结果
        return ResponseResult.SUCCESS("获取轮播图列表成功").setData(all);
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
        loopFromDb.setOrder(loop.getOrder());
        loopFromDb.setState(loop.getState());
        loopFromDb.setupdateTime(new Date());
        // 保存
        loopDao.save(loopFromDb);
        // 返回结果
        return ResponseResult.SUCCESS("轮播图更新成功！");
    }

    @Override
    public ResponseResult deleteLoop(String loopId) {
        int result = loopDao.deleteByUpdateState(loopId);
        return getDeleteResult(result,"轮播图");
    }

}