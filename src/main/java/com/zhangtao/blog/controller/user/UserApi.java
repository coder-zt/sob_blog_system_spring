package com.zhangtao.blog.controller.user;

import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IUserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public ResponseResult initManagerAccount(@RequestBody SobUser sobUser){
        log.info("userName ===> " + sobUser.getUserName());
        log.info("email ===> " + sobUser.getEmail());
        log.info("password ===> " + sobUser.getPassword());
        return userService.initManagerAccount(sobUser);
    }

    /**
     * 注册
     *
     * @param sobUser
     * @return
     */
    @PostMapping("/join_in")
    public ResponseResult register(@RequestBody SobUser sobUser,
                                   @RequestParam("verify_code") String emailCode
            , @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("captcha_key") String captchaKey){
        return userService.register(sobUser, emailCode, captchaCode, captchaKey);
    }

    /**
     * 登录
     *
     * 需要提交的数据：
     * 1. 用户账户（昵称、邮箱）
     * 2. 密码
     * 3. 图灵验证码
     * 4. 图灵验证码的key
     *
     * @param captchaKey 图灵验证码key
     * @param captcha 图灵验证码
     * @param sobUser 用户信息
     * @return
     */
    @PostMapping("/login/{captcha_key}/{captcha}")
    public ResponseResult login(@PathVariable("captcha_key") String captchaKey,
                                @PathVariable("captcha") String captcha,
                                @RequestBody SobUser sobUser, @RequestParam("from") String from){
        return userService.doLogin(sobUser, captchaKey, captcha,from);
    }


    /**
     * 获取图灵验证码
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(@RequestParam("captcha_key") String captchaKey) {
        try {
            userService.createCaptcha(captchaKey);
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
    public ResponseResult sendVerifyCode(@RequestParam("type") String type, @RequestParam("email")  String emailAddress){
        log.info("email address ===> " + emailAddress);
        return userService.sendEmail(type, emailAddress);
    }

    /**
     * 修改密码
     *  普通修改密码：
     *  比对旧密码修改新密码
     *
     *  既修改密码又可以找回密码：
     *  发送验证码到邮箱、手机验证通过后修改密码
     *
     * 步骤：
     * 1.用户填写邮箱
     * 2.用户获取验证码type=forget
     * 3.填写验证码
     * 4.填写新的密码
     * 5.提交数据
     *
     * 数据内容：
     * 1.邮箱和新密码
     * 2.验证码
     *
     * 如果验证码正确--->所用邮箱注册的账号就是你的可以修改密码
     * @param sobUser
     * @return
     */
    @PutMapping("/password")
    public ResponseResult updatePassword(@RequestParam("verifyCode") String verifyCode, @RequestBody SobUser sobUser){
        return userService.updatePassword(verifyCode, sobUser);
    }


    /**
     * 获取作者信息
     *
     * @param userId
     * @return
     */
    @GetMapping("user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId){
        return  userService.getUserInfo(userId);
    }

    /**
     * 更新用户信息
     *
     * 允许用户修改的内容
     * 1. 头像
     * 2. 用户名（唯一的）
     * 3. 签名
     * 4. 密码（单独修改）
     * 5. email (唯一的，单独修改)
     * @param sobUser
     * @return
     */
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId, @RequestBody SobUser sobUser){
        return userService.updateUserInfo(userId, sobUser);
    }

    /**
     * 获取用户列表
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page") int page, @RequestParam("size") int size){
        return userService.listUsers(page, size);
    }

    /**
     * 删除用户
     *
     * 需要管理员的权限
     *
     * @param userId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId")String userId){
        //需要判断用户权限
        // TODO: 2021/10/9 通过注解的方式来控制权限
        return userService.deleteUser(userId);
    }


    /**
     * 检查该email是否已经注册
     *
     * @param email 邮箱地址
     * @return SUCCESS ---> 已经注册，FAILED ---> 没有注册
     */
    @ApiResponses({
            @ApiResponse(code = 2000, message = "表示当前邮箱已经注册了"),
            @ApiResponse(code = 2000, message = "表示当前邮箱未注册")
    })
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email") String email){
        return userService.checkEmail(email);
    }

    /**
     * 检查userName是否已经注册
     *
     * @param userName 邮箱地址
     * @return SUCCESS ---> 已经注册，FAILED ---> 没有注册
     */
    @ApiResponses({
            @ApiResponse(code = 2000, message = "表示userName已经注册了"),
            @ApiResponse(code = 2000, message = "表示userName未注册")
    })
    @GetMapping("/user_name")
    public ResponseResult checkUserName(@RequestParam("userName") String userName){
        return userService.checkUserName(userName);
    }

    /**
     * 修改条件：
     * 必须是登录状态
     * 新的邮箱没有被注册
     *
     * 用户的步骤
     * 1、已经登录
     * 2、输入信的邮箱地址
     * 3、获取邮箱验证码 type=update
     * 4、输入验证码
     * 5、提交数据
     * <p>
     * 提交的数据：
     * 1、新的邮箱地址
     * 2、验证码
     * 3、其他信息通过token获取
     *
     * @return
     */
    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("email") String email, @RequestParam("verify_code") String verifyCode){
        return userService.updateEmail(email, verifyCode);
    }

    /**
     * 退出登录
     *
     * 拿到tokenKey
     * 1、删除redis中的token
     * 2、删除mysql中的refreshToken
     * 3、删除cookie中的tokenkey
     *
     * @return
     */
    @GetMapping("/logout")
    public ResponseResult logout(){
        return userService.doLogout();
    }
}
