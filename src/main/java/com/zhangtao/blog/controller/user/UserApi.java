package com.zhangtao.blog.controller.user;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.services.impl.UserServiceImpl;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.RedisUtils;
import com.zhangtao.blog.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

/**
 * - 用户user
 *   - 初始化管理员账号-init-admin
 *   - 注册join-in
 *   - 登录sign-up
 *   - 获取人类验证码captcha
 *   - 发送邮件email
 *   - 修改密码password
 *   - 获取作者信息user-info
 *   - 修改用户信息user-info
 *   - 获取用户列表
 *   - 删除用户
 *   - 重置用户密码
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {


    @Autowired
    private IUserService userService;


    /**
     * 初始化管理员账号-init-admin
     * @param sobUser
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody SobUser sobUser, HttpServletRequest request){
        log.info("userName ===> " + sobUser.getUserName());
        log.info("email ===> " + sobUser.getEmail());
        log.info("password ===> " + sobUser.getPassword());
        return userService.initManagerAccount(sobUser, request);
    }

    /**
     * 注册
     *
     * @param sobUser
     * @return
     */
    @PostMapping
    public ResponseResult register(@RequestBody SobUser sobUser,
                                   @RequestParam("verify_code") String emailCode
            , @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("captcha_key") String captchaKey,
                                   HttpServletRequest request){
        return userService.register(sobUser, emailCode, captchaCode, captchaKey,request);
    }

    /**
     * 登录
     *
     * @param sobUser
     * @return
     */
    @PostMapping("/{captcha}")
    public ResponseResult login(@PathVariable("captcha") String captcha, @RequestBody SobUser sobUser){
        return null;
    }


    /**
     * 获取图灵验证码
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("captcha_key") String captchaKey) {
        try {
            userService.createCaptcha(response, captchaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件email
     *
     * 使用场景：
     *      注册：邮箱已经注册过来需要提示已注册
     *      修改邮箱（新的邮箱）：邮箱已经注册过来需要提示已注册
     *      找回密码：如果邮箱地址不存在则提示为注册该邮箱
     *
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request,@RequestParam("type") String type, @RequestParam("email")  String emailAddress){
        log.info("email address ===> " + emailAddress);
        return userService.sendEmail(request, type, emailAddress);
    }

    /**
     * 修改密码
     *
     * @param sobUser
     * @return
     */
    @PutMapping("/password/{userId}")
    public ResponseResult updatePassword(@PathVariable("userId") String userId, @RequestBody SobUser sobUser){
        return null;
    }


    /**
     * 获取作者信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId){
        return  null;
    }

    /**
     * 更新用户信息
     *
     * @param sobUser
     * @return
     */
    @PutMapping("/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId, @RequestBody SobUser sobUser){
        return null;
    }

    /**
     * 获取用户列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId")String userId){
        return null;
    }
}
