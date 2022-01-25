package com.zhangtao.blog.services;

import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile file, String original);

     void viewImage(String imageId) throws IOException;

    ResponseResult listImages(int page, int size, String original);

    ResponseResult deleteImage(String imageId);

    void getQrCodeImage(String code) throws IOException;
}
