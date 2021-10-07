package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.pojo.Images;
import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心图片的api
 */
@RestController
@RequestMapping("/admin/image")
public class ImageApi {

    /**
     * 添加图片
     *
     * @return
     */
    @PostMapping
    public ResponseResult uploadImage(){
        return null;
    }

    /**
     * 删除图片
     *
     * @param imageId
     * @return
     */
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId){
        return null;
    }

    /**
     * 修改图片
     *
     * @param imageId
     * @return
     */
    @PutMapping("/{imageId}")
    public ResponseResult updateImage(@PathVariable("imageId") String imageId, @RequestBody Images image){
        return null;
    }

    /**
     * 获取图片
     *
     * @param imageId
     * @return
     */
    @GetMapping("/{imageId}")
    public ResponseResult getImage(@PathVariable("imageId") String imageId){
        return null;
    }

    /**
     * 获取图片列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult listImage(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }
}
