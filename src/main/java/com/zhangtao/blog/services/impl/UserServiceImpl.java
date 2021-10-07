package com.zhangtao.blog.services.impl;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.zhangtao.blog.dao.SettingsDao;
import com.zhangtao.blog.dao.UserDao;
import com.zhangtao.blog.pojo.Settings;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.Random;


@Service
@Transactional
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SettingsDao settingsDao;

    public static final int[] captcha_font_types = {Captcha.FONT_1,
            Captcha.FONT_2,
            Captcha.FONT_3,
            Captcha.FONT_4,
            Captcha.FONT_5,
            Captcha.FONT_6,
            Captcha.FONT_7,
            Captcha.FONT_8,
            Captcha.FONT_9,
            Captcha.FONT_10
    };

    @Autowired
    Random random;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request) {
        //todo: 检查是否以初始化
        Settings managerInitState = settingsDao.findOneByKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerInitState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化！");
        }
        //检查数据
        if(TextUtils.isEmpty(sobUser.getUserName())){
            return ResponseResult.FAILED("用户名不能为空");
        }
        if(TextUtils.isEmpty(sobUser.getPassword())){
            return ResponseResult.FAILED("密码不能为空");
        }
        if(TextUtils.isEmpty(sobUser.getEmail())){
            return ResponseResult.FAILED("email不能为空");
        }
        //密码加密
        String password = sobUser.getPassword();
        String encode = passwordEncoder.encode(password);
        sobUser.setPassword(encode);
        //补充数据
        sobUser.setId(String.valueOf(idWorker.nextId()));
        sobUser.setRoles(Constants.User.ROLE_ADMIN);
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setState(Constants.User.DEFAULT_STATE);
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        log.info("remoteAddr ===> " + remoteAddr);
        log.info("localAddr ===> " + localAddr);
        sobUser.setLoginIp(remoteAddr);
        sobUser.setRegIp(remoteAddr);
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());
        //保存到数据库
        userDao.save(sobUser);
        //更新标记
        Settings settings  = new Settings();
        settings.setId(idWorker.nextId() + "");
        settings.setCreate_time(new Date());
        settings.setUpdate_time(new Date());
        settings.setKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        settings.setValue("1");
        settingsDao.save(settings);
        return ResponseResult.SUCCESS("管理员账号初始化成功");
    }

    @Override
    public void createCaptcha(HttpServletResponse response, String captchaKey) throws Exception{
        if(TextUtils.isEmpty(captchaKey) || captchaKey.length() < 13){
            return;
        }
        long key;
        try{
            key = Long.parseLong(captchaKey);
        }catch (Exception e){
            return;
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        int captchaType = random.nextInt(3);
        Captcha targetCaptcha = null;
        switch (captchaType){
            case 0:
                // 三个参数分别为宽、高、位数
                targetCaptcha = new SpecCaptcha(200,60, 5);
                break;
            case 1://gif类型
                targetCaptcha = new GifCaptcha(200,60);
                break;
            case 2:// 算术类型
                targetCaptcha = new ArithmeticCaptcha(200,60);
                targetCaptcha.setLen(2);
                String arithmeticStr = ((ArithmeticCaptcha)targetCaptcha).getArithmeticString();
                log.info("ArithmeticCaptcha string is ===> " + arithmeticStr);
                break;
        }
        int index = random.nextInt(captcha_font_types.length);
        log.info("captcha font type index ===> " + index);
        targetCaptcha.setFont(captcha_font_types[index]);
        targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        String content = targetCaptcha.text().toLowerCase();
        log.info("captcha content ===> " + content);
        //保存到redis中
        redisUtils.set(Constants.User.KEY_REDIS_CAPTCHA + key, content, 60 * 10);
        // 输出图片流
        targetCaptcha.out(response.getOutputStream());
    }

    @Autowired
    private TaskService taskService;

    /**
     * 发送邮件验证码
     * @param request
     * @param emailAddress
     * @return
     */
    @Override
    public ResponseResult sendEmail(HttpServletRequest request, String emailAddress) {
        // 1、防止暴力发送：同一个邮箱，间隔30s发一次，同一个IP，1个小时内最多只能发送10次
        String remoteAddress = request.getRemoteAddr();
        remoteAddress = remoteAddress.replaceAll(":", "_");
        log.info("sendEmail > ip ===> " + remoteAddress);
        //
        Integer ipSendTime = (Integer)redisUtils.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddress);
        if(ipSendTime != null && ipSendTime > 10){
            return ResponseResult.FAILED("操作频繁!");
        }
        Object hasSend = redisUtils.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if(hasSend != null){
            return ResponseResult.FAILED("操作频繁!");
        }
        // 2、检查邮箱地址是否正确
        boolean isEmailAddressOk = TextUtils.isEmailAddressOk(emailAddress);
        if (!isEmailAddressOk) {
            return ResponseResult.FAILED("邮箱错误!");
        }
        // 3、发送验证码,6位数：100000~999999
        int code = random.nextInt(999999);
        if(code < 100000){
            code += 100000;
        }
        log.info("sendEmail ===> code ===> " + code);
        try {
            taskService.sendEmailVerifyCode(String.valueOf(code), emailAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.FAILED("系统出错，请稍后重试");
        }
        // 4、做记录
        if(ipSendTime == null){
            ipSendTime = 0;
        }
        ipSendTime++;
        //1个小时有效期
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddress, ipSendTime, 60 * 60);
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", 30);
        redisUtils.set(Constants.User.KEY_EMAIL_CODE_CONTENT + emailAddress, String.valueOf(code), 60 * 10);
        return ResponseResult.SUCCESS("操作成功!");
    }
}
