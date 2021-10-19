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

    @GetMapping("/{imageId}")
    public void viewImage(@PathVariable("imageId") String imageId){
        try {
            imageService. viewImage(imageId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
