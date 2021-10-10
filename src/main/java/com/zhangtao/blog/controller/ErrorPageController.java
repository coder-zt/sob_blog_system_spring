package com.zhangtao.blog.controller;

import com.zhangtao.blog.responese.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误状态应答
 */
@RestController
public class ErrorPageController {

    @GetMapping("/403")
    public ResponseResult page403(){
        return ResponseResult.ERROR_403();
    }

    @GetMapping("/404")
    public ResponseResult page404(){
        return ResponseResult.ERROR_404();
    }

    @GetMapping("/504")
    public ResponseResult page504(){
        return ResponseResult.ERROR_504();
    }

    @GetMapping("/505")
    public ResponseResult page505(){
        return ResponseResult.ERROR_505();
    }

}
