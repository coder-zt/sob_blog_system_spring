package com.zhangtao.blog.services;

import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile file);

     void viewImage(String imageId) throws IOException;

    ResponseResult listImages(int page, int size);

    ResponseResult deleteImage(String imageId);
}
