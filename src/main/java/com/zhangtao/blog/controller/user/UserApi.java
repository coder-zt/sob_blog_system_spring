package com.zhangtao.blog.controller.user;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IUserService;
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
    public ResponseResult register(@RequestBody SobUser sobUser){
        //第一步：检查当前用户名是否已经注册
        //第二步：检查邮箱格式是否正确
        //第三步：检查该邮箱是否已经注册
        //第四步：检查邮箱验证码是否正确
        //第五步：检查图灵验证码是否正确
        //达到可以注册的条件
        //第六步：对密码进行加密
        //第七步：补全数据
        //包括：注册IP,登录IP,角色,头像,创建时间,更新时间
        //第八步：保存到数据库中
        //第九步：返回结果
        return null;
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
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request, @RequestParam("email")  String emailAddress){
        log.info("email address ===> " + emailAddress);
        return userService.sendEmail(request, emailAddress);
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
