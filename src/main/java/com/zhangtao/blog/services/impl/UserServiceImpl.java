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
import com.zhangtao.blog.responese.ResponseState;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
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
     *
     *  * 使用场景：
     *      *      注册(register)：邮箱已经注册过来需要提示已注册
     *      *      修改邮箱（新的邮箱）(update)：邮箱已经注册过来需要提示已注册
     *      *      找回密码(forget)：如果邮箱地址不存在则提示为注册该邮箱
     * @param request
     * @param emailAddress
     * @return
     */
    @Override
    public ResponseResult sendEmail(HttpServletRequest request,String type, String emailAddress) {
        if (TextUtils.isEmpty(emailAddress)) {
            return ResponseResult.FAILED("邮箱地址不可以为空!");
        }
        //根据类型查询邮箱是否存在
        if("register".equals(type) || "forget".equals(type)){
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail != null) {
                return ResponseResult.FAILED("该邮箱地址已注册!");
            }
        }else{
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail == null) {
                return ResponseResult.FAILED("该邮箱地未注册!");
            }
        }
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
            return ResponseResult.FAILED("邮箱地址错误!");
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

    @Override
    public ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request) {
        log.info("emailCode ===> " + emailCode);
        log.info("captchaCode ===> " + captchaCode);
        log.info("captchaKey ===> " + captchaKey);
        //第一步：检查当前用户名是否已经注册
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("用户名不可以为空!");
        }
        SobUser userFromDbByUserName = userDao.findOneByUserName(userName);
        if (userFromDbByUserName != null) {
            return ResponseResult.FAILED("用户名已注册!");
        }
        //第二步：检查邮箱格式是否正确
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱地址不可以为空!");
        }
        boolean isEmailAddressOk = TextUtils.isEmailAddressOk(email);
        if (!isEmailAddressOk) {
            return ResponseResult.FAILED("邮箱地址错误!");
        }
        //第三步：检查该邮箱是否已经注册
        SobUser userByEmail = userDao.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("邮箱地址已注册!");
        }
        //第四步：检查邮箱验证码是否正确
        String emailVerifyCode = (String)redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(emailVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码无效!");
        }
        if(!emailVerifyCode.equals(emailCode)){
            return ResponseResult.FAILED("邮箱验证码错误!");
        }else{
            //正确，删除redis中的记录
            redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        }
        //第五步：检查图灵验证码是否正确
        String captchaVerifyCode = (String)redisUtils.get(Constants.User.KEY_REDIS_CAPTCHA + captchaKey);
        if (TextUtils.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("人类验证码无效!");
        }
        if(!captchaVerifyCode.equals(captchaCode)){
            return ResponseResult.FAILED("人类验证码错误!");
        }else{
            //正确，删除redis中的记录
            redisUtils.del(Constants.User.KEY_REDIS_CAPTCHA + captchaKey);
        }
        //达到可以注册的条件
        //第六步：对密码进行加密
        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空!");
        }
        sobUser.setPassword(passwordEncoder.encode(password));
        //第七步：补全数据
        String ipAddress = request.getRemoteAddr();
        sobUser.setRegIp(ipAddress);
        sobUser.setLoginIp(ipAddress);
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setRoles(Constants.User.ROLE_NORMAL);
        sobUser.setState("1");
        sobUser.setId(idWorker.nextId() + "");
        //包括：注册IP,登录IP,角色,头像,创建时间,更新时间
        //第八步：保存到数据库中
        userDao.save(sobUser);
        //第九步：返回结果
        return ResponseResult.GET(ResponseState.REGISTER_SUCCESS);
    }
}
