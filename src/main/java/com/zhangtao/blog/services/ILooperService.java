package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.Looper;
import com.zhangtao.blog.responese.ResponseResult;

public interface ILooperService {

    ResponseResult addLooper(Looper looper);

    ResponseResult listLoop();

    ResponseResult getLoop(String looperId);

    ResponseResult updateLoop(String loopId, Looper loop);

    ResponseResult deleteLoop(String loopId);

}