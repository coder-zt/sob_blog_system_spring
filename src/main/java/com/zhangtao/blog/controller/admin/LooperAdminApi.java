package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.interceptor.CheckTooFrequentCommit;
import com.zhangtao.blog.pojo.Looper;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ILooperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心分类的api
 */
@RestController
@RequestMapping("/admin/looper")
public class LooperAdminApi {

    @Autowired
    private ILooperService looperService;

    /**
     * 添加分类
     *
     * @param loop
     * @return
     */
    @CheckTooFrequentCommit
    @PostMapping
    public ResponseResult addLooper(@RequestBody Looper looper) {
        return looperService.addLooper(looper);
    }

    /**
     * 删除分类
     *
     * @param loopId
     * @return
     */
    @DeleteMapping("/{loopId}")
    public ResponseResult deleteLoop(@PathVariable("loopId") String loopId) {
        return looperService.deleteLoop(loopId);
    }

    /**
     * 修改分类
     *
     * @param loopId
     * @return
     */
    @PutMapping("/{loopId}")
    public ResponseResult updateLoop(@PathVariable("loopId") String loopId, @RequestBody Looper loop) {
        return looperService.updateLoop(loopId, loop);
    }

    /**
     * 获取分类
     *
     * @param looperId
     * @return
     */
    @GetMapping("/{loopId}")
    public ResponseResult getLoop(@PathVariable("loopId") String looperId) {
        return looperService.getLoop(looperId);
    }

    /**
     * 获取分类列表
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseResult listLoop() {
        return looperService.listLoop();
    }

}
