package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.pojo.Looper;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/admin/looper")
public class LooperApi {

    /**
     * 添加分类
     *
     * @param loop
     * @return
     */
    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper loop){
        return null;
    }

    /**
     * 删除分类
     *
     * @param loopId
     * @return
     */
    @DeleteMapping("/{loopId}")
    public ResponseResult deleteLoop(@PathVariable("loopId") String loopId){
        return null;
    }

    /**
     * 修改分类
     *
     * @param loopId
     * @return
     */
    @PutMapping("/{loopId}")
    public ResponseResult updateLoop(@PathVariable("loopId") String loopId, @RequestBody Looper loop){
        return null;
    }

    /**
     * 获取分类
     *
     * @param looperId
     * @return
     */
    @GetMapping("/{loopId}")
    public ResponseResult getLoop(@PathVariable("loopId") String looperId){
        return null;
    }

    /**
     * 获取分类列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult listLoop(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }


    @PutMapping("/state/{articleId}/{state}")
    public ResponseResult updateArticleState(@PathVariable("articleId") String articleId, @PathVariable("state")String state){

        return null;
    }

    @PutMapping("/top/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId") String articleId){

        return null;
    }
}
