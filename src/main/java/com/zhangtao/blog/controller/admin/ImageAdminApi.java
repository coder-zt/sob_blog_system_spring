package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 *管理中心图片的api
 */
@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {


    @Autowired
    private IImageService imageService;
    /**
     * 添加图片
     *
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @PostMapping
    public ResponseResult uploadImage(MultipartFile file){
        return imageService.uploadImage(file);
    }

    /**
     * 删除图片
     *
     * @param imageId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId){
        return imageService.deleteImage(imageId);
    }


    /**
     * 获取图片列表
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listImages(@PathVariable("page") int page, @PathVariable("size") int size){
        return imageService.listImages(page, size);
    }
}
