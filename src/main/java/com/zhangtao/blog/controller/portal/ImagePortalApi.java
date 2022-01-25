package com.zhangtao.blog.controller.portal;

import com.zhangtao.blog.services.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/portal/image")
public class ImagePortalApi {

    @Autowired
    private IImageService imageService;

    @GetMapping("/{imageId:.+}")//{fileName:.+}
    public void viewImage(@PathVariable("imageId") String imageId){
        try {
            imageService. viewImage(imageId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/qr-code/{code}")
    public void getQrCodeImage(@PathVariable("code") String code){
       //生成二维码
        //二维码内容是什么？
        try {
            imageService.getQrCodeImage(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
