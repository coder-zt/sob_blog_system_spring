package com.zhangtao.blog.services.impl;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.google.gson.Gson;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.zhangtao.blog.dao.RefreshTokenDao;
import com.zhangtao.blog.dao.SettingsDao;
import com.zhangtao.blog.dao.UserDao;
import com.zhangtao.blog.pojo.RefreshToken;
import com.zhangtao.blog.pojo.Settings;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.responese.ResponseState;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.ClaimsUtils;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.CookieUtils;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.JwtUtil;
import com.zhangtao.blog.utils.RedisUtils;
import com.zhangtao.blog.utils.TextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

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

    @Autowired
    private Gson gson;

    @Autowired
    private RefreshTokenDao refreshTokenDao;

    public static final int[] captcha_font_types = { Captcha.FONT_1, Captcha.FONT_2, Captcha.FONT_3, Captcha.FONT_4,
            Captcha.FONT_5, Captcha.FONT_6, Captcha.FONT_7, Captcha.FONT_8, Captcha.FONT_9, Captcha.FONT_10 };

    @Autowired
    Random random;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public ResponseResult initManagerAccount(SobUser sobUser) {
        // todo: 检查是否以初始化
        Settings managerInitState = settingsDao.findOneByKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerInitState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化！");
        }
        // 检查数据
        if (TextUtils.isEmpty(sobUser.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getEmail())) {
            return ResponseResult.FAILED("email不能为空");
        }
        // 密码加密
        String password = sobUser.getPassword();
        String encode = passwordEncoder.encode(password);
        sobUser.setPassword(encode);
        // 补充数据
        sobUser.setId(String.valueOf(idWorker.nextId()));
        sobUser.setRoles(Constants.User.ROLE_ADMIN);
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setState(Constants.User.DEFAULT_STATE);
        String remoteAddr = getRequest().getRemoteAddr();
        String localAddr = getRequest().getLocalAddr();
        log.info("remoteAddr ===> " + remoteAddr);
        log.info("localAddr ===> " + localAddr);
        sobUser.setLoginIp(remoteAddr);
        sobUser.setRegIp(remoteAddr);
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());
        // 保存到数据库
        userDao.save(sobUser);
        // 更新标记
        Settings settings = new Settings();
        settings.setId(idWorker.nextId() + "");
        settings.setCreate_time(new Date());
        settings.setUpdate_time(new Date());
        settings.setKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        settings.setValue("1");
        settingsDao.save(settings);
        return ResponseResult.SUCCESS("管理员账号初始化成功");
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 创建图灵验证码
     *
     * @param captchaKey
     * @throws Exception
     */
    @Override
    public void createCaptcha(String captchaKey) throws Exception {
        if (TextUtils.isEmpty(captchaKey) || captchaKey.length() < 13) {
            return;
        }
        long key;
        try {
            key = Long.parseLong(captchaKey);
        } catch (Exception e) {
            return;
        }

        // 设置请求头为输出图片类型
        getResponse().setContentType("image/gif");
        getResponse().setHeader("Pragma", "No-cache");
        getResponse().setHeader("Cache-Control", "no-cache");
        getResponse().setDateHeader("Expires", 0);
        int captchaType = random.nextInt(3);
        Captcha targetCaptcha = null;
        switch (captchaType) {
            case 0:
                // 三个参数分别为宽、高、位数
                targetCaptcha = new SpecCaptcha(200, 60, 5);
                break;
            case 1:// gif类型
                targetCaptcha = new GifCaptcha(200, 60);
                break;
            case 2:// 算术类型
                targetCaptcha = new ArithmeticCaptcha(200, 60);
                targetCaptcha.setLen(2);
                String arithmeticStr = ((ArithmeticCaptcha) targetCaptcha).getArithmeticString();
                log.info("ArithmeticCaptcha string is ===> " + arithmeticStr);
                break;
        }
        int index = random.nextInt(captcha_font_types.length);
        log.info("captcha font type index ===> " + index);
        targetCaptcha.setFont(captcha_font_types[index]);
        targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        String content = targetCaptcha.text().toLowerCase();
        log.info("captcha content ===> " + content);
        // 保存到redis中
        redisUtils.set(Constants.User.KEY_REDIS_CAPTCHA + key, content, 60 * 10);
        // 输出图片流
        targetCaptcha.out(getResponse().getOutputStream());
    }

    @Autowired
    private TaskService taskService;

    /**
     * 发送邮件验证码
     *
     * * 使用场景： * 注册(register)：邮箱已经注册过来需要提示已注册 * 修改邮箱（新的邮箱）(update)：邮箱已经注册过来需要提示已注册 *
     * 找回密码(forget)：如果邮箱地址不存在则提示为注册该邮箱
     * 
     * @param emailAddress
     * @return
     */
    @Override
    public ResponseResult sendEmail(String type, String emailAddress) {
        if (TextUtils.isEmpty(emailAddress)) {
            return ResponseResult.FAILED("邮箱地址不可以为空!");
        }
        // 根据类型查询邮箱是否存在
        if ("register".equals(type) || "update".equals(type)) {
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail != null) {
                return ResponseResult.FAILED("该邮箱地址已注册!");
            }
        } else {
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail == null) {
                return ResponseResult.FAILED("该邮箱地未注册!");
            }
        }
        // 1、防止暴力发送：同一个邮箱，间隔30s发一次，同一个IP，1个小时内最多只能发送10次
        String remoteAddress = getRequest().getRemoteAddr();
        remoteAddress = remoteAddress.replaceAll(":", "_");
        log.info("sendEmail > ip ===> " + remoteAddress);
        //
        Integer ipSendTime = (Integer) redisUtils.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddress);
        if (ipSendTime != null && ipSendTime > 10) {
            return ResponseResult.FAILED("操作频繁!");
        }
        Object hasSend = redisUtils.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if (hasSend != null) {
            return ResponseResult.FAILED("操作频繁!");
        }
        // 2、检查邮箱地址是否正确
        boolean isEmailAddressOk = TextUtils.isEmailAddressOk(emailAddress);
        if (!isEmailAddressOk) {
            return ResponseResult.FAILED("邮箱地址错误!");
        }
        // 3、发送验证码,6位数：100000~999999
        int code = random.nextInt(999999);
        if (code < 100000) {
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
        if (ipSendTime == null) {
            ipSendTime = 0;
        }
        ipSendTime++;
        // 1个小时有效期
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddress, ipSendTime, 60 * 60);
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", 30);
        redisUtils.set(Constants.User.KEY_EMAIL_CODE_CONTENT + emailAddress, String.valueOf(code), 60 * 10);
        return ResponseResult.SUCCESS("操作成功!");
    }

    @Override
    public ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey) {
        log.info("emailCode ===> " + emailCode);
        log.info("captchaCode ===> " + captchaCode);
        log.info("captchaKey ===> " + captchaKey);
        // 第一步：检查当前用户名是否已经注册
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("用户名不可以为空!");
        }
        SobUser userFromDbByUserName = userDao.findOneByUserName(userName);
        if (userFromDbByUserName != null) {
            return ResponseResult.FAILED("用户名已注册!");
        }
        // 第二步：检查邮箱格式是否正确
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱地址不可以为空!");
        }
        boolean isEmailAddressOk = TextUtils.isEmailAddressOk(email);
        if (!isEmailAddressOk) {
            return ResponseResult.FAILED("邮箱地址错误!");
        }
        // 第三步：检查该邮箱是否已经注册
        SobUser userByEmail = userDao.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("邮箱地址已注册!");
        }
        // 第四步：检查邮箱验证码是否正确
        String emailVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(emailVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码无效!");
        }
        if (!emailVerifyCode.equals(emailCode)) {
            return ResponseResult.FAILED("邮箱验证码错误!");
        } else {
            // 正确，删除redis中的记录
            redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        }
        // 第五步：检查图灵验证码是否正确
        String captchaVerifyCode = (String) redisUtils.get(Constants.User.KEY_REDIS_CAPTCHA + captchaKey);
        if (TextUtils.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("人类验证码无效!");
        }
        if (!captchaVerifyCode.equals(captchaCode)) {
            return ResponseResult.FAILED("人类验证码错误!");
        } else {
            // 正确，删除redis中的记录
            redisUtils.del(Constants.User.KEY_REDIS_CAPTCHA + captchaKey);
        }
        // 达到可以注册的条件
        // 第六步：对密码进行加密
        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空!");
        }
        sobUser.setPassword(passwordEncoder.encode(password));
        // 第七步：补全数据
        String ipAddress = getRequest().getRemoteAddr();
        sobUser.setRegIp(ipAddress);
        sobUser.setLoginIp(ipAddress);
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setRoles(Constants.User.ROLE_NORMAL);
        sobUser.setState("1");
        sobUser.setId(idWorker.nextId() + "");
        // 包括：注册IP,登录IP,角色,头像,创建时间,更新时间
        // 第八步：保存到数据库中
        userDao.save(sobUser);
        // 第九步：返回结果
        return ResponseResult.GET(ResponseState.REGISTER_SUCCESS);
    }

    @Override
    public ResponseResult doLogin(SobUser sobUser, String captchaKey, String captcha) {
        // 1. 判断图灵验证码是否正确
        String captchaValue = (String) redisUtils.get(Constants.User.KEY_REDIS_CAPTCHA + captchaKey);
        if (TextUtils.isEmpty(captchaValue) || !captchaValue.equals(captcha)) {
            redisUtils.del(Constants.User.KEY_REDIS_CAPTCHA + captchaKey);
            return ResponseResult.FAILED("人类验证码错误");
        }
        redisUtils.del(Constants.User.KEY_REDIS_CAPTCHA + captchaKey);
        // 2. 对用户信息判空
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("账号不可以为空");
        }
        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        // 3. 查询用户信息
        SobUser userFromDb = userDao.findOneByUserName(userName);
        if (userFromDb == null) {
            userFromDb = userDao.findOneByEmail(userName);
        }
        if (userFromDb == null) {
            return ResponseResult.FAILED("找不到该用户信息");
        }
        // 4. 验证密码是否正确
        boolean matches = passwordEncoder.matches(password, userFromDb.getPassword());
        if (!matches) {
            return ResponseResult.FAILED("用户名或密码错误");
        }
        // 5. 判断用户状态
        if (!"1".equals(userFromDb.getState())) {
            return ResponseResult.FAILED("当前账号已被禁止使用");
        }
        createToken(userFromDb);
        return ResponseResult.SUCCESS("登录成功");
    }

    private String createToken(SobUser userFromDb) {
        int result = refreshTokenDao.deleteByUserId(userFromDb.getId());
        log.info("删除用户的旧refreshToken ===> " + result);
        // 6. 生成token（有效时常两个小时）
        Map<String, Object> claims = ClaimsUtils.sobUser2Claims(userFromDb);
        String token = JwtUtil.createToken(claims);
        // 7.将token的md5值返回给前端并作为key将token保存在redis中
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        redisUtils.set(Constants.User.KEY_TOKEN + tokenKey, token, Constants.TimeValue.HOUR_2);
        // 8. 设置cookie
        CookieUtils.setUpCookie(getResponse(), Constants.User.COOKICE_TOKEN_EKY, tokenKey);
        // 9. 创建RefreshToken
        String refreshTokenValue = JwtUtil.createRefreshToken(userFromDb.getId(), Constants.TimeValue.MONTH);
        // TODO: 2021/10/8 保存到数据库
        // refreshToken、 tokenKey、 用户ID、 创建时间、 更新时间
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(idWorker.nextId() + "");
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userFromDb.getId());
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenDao.save(refreshToken);
        return tokenKey;
    }

    /**
     * 验证登录是否过期
     *
     * @return
     */
    @Override
    public SobUser checkSobUser() {
        // 获取tokenKey
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKICE_TOKEN_EKY);
        SobUser sobUser = parseByTokenKey(tokenKey);
        if (sobUser == null) {// 登录已过期
            // 1.在mysql查询refreshToken
            RefreshToken refreshToken = refreshTokenDao.findOneByTokenKey(tokenKey);
            if (refreshToken == null) {// 2. 不存在证明用户没有登录小于要重新登录
                return null;
            }
            // 3. 存在则解析refreshToken
            try {
                JwtUtil.parseJWT(refreshToken.getRefreshToken());
                // 5. 没有报错证明refreshToken有效，创建新的token和新的refreshToken
                String userId = refreshToken.getUserId();
                SobUser userFromDb = userDao.findOneById(userId);
                String newTokenKey = createToken(userFromDb);
                return parseByTokenKey(newTokenKey);
            } catch (Exception e) {
                // 4. refreshToken也过期了
                return null;
            }
        }
        return sobUser;
    }

    /**
     * 查询用户信息
     * 
     * @param userId
     * @return
     */
    @Override
    public ResponseResult getUserInfo(String userId) {
        // 从数据库中获取
        SobUser sobUser = userDao.findOneById(userId);
        // 判断结果
        if (sobUser == null) {
            return ResponseResult.FAILED("用户信息不存在");
        }
        // 如果存在则隐藏一些关键信息
        String userJson = gson.toJson(sobUser);
        SobUser newSobUser = gson.fromJson(userJson, SobUser.class);
        newSobUser.setPassword("");
        newSobUser.setEmail("");
        newSobUser.setRegIp("");
        newSobUser.setLoginIp("");
        // 返回结果
        return ResponseResult.SUCCESS("获取成功").setData(newSobUser);
    }

    @Override
    public ResponseResult checkEmail(String email) {
        log.info("check email is " + email);
        SobUser user = userDao.findOneByEmail(email);
        return user == null ? ResponseResult.FAILED("该邮箱未注册") : ResponseResult.SUCCESS("该邮箱已注册");
    }

    @Override
    public ResponseResult checkUserName(String userName) {
        log.info("check userName is " + userName);
        SobUser user = userDao.findOneByUserName(userName);
        return user == null ? ResponseResult.FAILED("该用户名未注册") : ResponseResult.SUCCESS("该用户名已注册");
    }

    /**
     * 更新用户信息
     *
     * @param userId
     * @param sobUser
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(String userId, SobUser sobUser) {
        SobUser userFormTokenKey = checkSobUser();
        if (userFormTokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        SobUser userFromDb = userDao.findOneById(userFormTokenKey.getId());
        // 判断用户的ID是否一致
        if (!userFromDb.getId().equals(userId)) {
            return ResponseResult.PERMISSION_FORBID();
        }
        // 修改的属性：头像、签名、用户名
        String userName = sobUser.getUserName();
        if (!TextUtils.isEmpty(userName)) {
            SobUser userByUserName = userDao.findOneByUserName(userName);
            if (userByUserName != null) {
                return ResponseResult.FAILED("该用户名已注册");
            }
            userFromDb.setUserName(userName);
        }
        if (!TextUtils.isEmpty(sobUser.getAvatar())) {
            userFromDb.setAvatar(sobUser.getAvatar());
        }
        userFromDb.setSign(sobUser.getSign());
        userDao.save(userFromDb);
        // 修改用户信息后，redis中的token的信息已经不正确了，所以需要删除token，下次请求就会重新生成新的token
        // 获取到当前权限所有的角色，进行角色对比即可确定权限
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKICE_TOKEN_EKY);
        redisUtils.del(Constants.User.KEY_TOKEN + tokenKey);
        return ResponseResult.SUCCESS("用户信息更新成功");
    }

    /**
     * 删除用户只是修改用户的状态
     *
     * 需要管理员权限
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult deleteUser(String userId) {
        // 修改用户状态
        int result = userDao.deleteUserById(userId);
        if (result > 0) {
            return ResponseResult.SUCCESS("删除用户成功");
        } else {
            return ResponseResult.SUCCESS("用户不存在");
        }
    }

    @Override
    public ResponseResult listUsers(int page, int size) {
        if (page < Constants.Page.DEFAULT_PAGE) {
            page = Constants.Page.DEFAULT_PAGE;
        }
        if (size < Constants.Page.MINI_PAGE_SIZE) {
            size = Constants.Page.MINI_PAGE_SIZE;
        }
        // 开始查询用户数据
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        Page<SobUser> all = userDao.listAllUserNoPassword(pageable);
        return ResponseResult.SUCCESS("获取用户列表成功").setData(all);
    }

    @Override
    public ResponseResult updatePassword(String verifyCode, SobUser sobUser) {
        // 检查email不能为空
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱地址不可以为空");
        }
        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        // 检查邮箱验证码是否正确
        String emailVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (emailVerifyCode == null || !emailVerifyCode.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码错误");
        }
        redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        // 修改密码
        String encodePassword = passwordEncoder.encode(sobUser.getPassword());
        int result = userDao.updatePasswordByEmail(encodePassword, email);
        return result > 0 ? ResponseResult.SUCCESS("密码修改成功") : ResponseResult.FAILED("密码修改失败");
    }

    @Override
    public ResponseResult updateEmail(String email, String verifyCode) {
        // 验证登录
        SobUser sobUser = checkSobUser();
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        // 验证邮箱验证码
        String emailVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (!emailVerifyCode.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码错误");
        }
        redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        // 修改邮箱
        int result = userDao.updateEmailById(email, sobUser.getId());
        return result > 0 ? ResponseResult.SUCCESS("邮箱修改成功") : ResponseResult.FAILED("邮箱修改失败");
    }

    @Override
    public ResponseResult doLogout() {
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKICE_TOKEN_EKY);
        if (TextUtils.isEmpty(tokenKey)) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        // 删除redis中的token
        redisUtils.del(Constants.User.KEY_TOKEN + tokenKey);
        // 删除mysql中的refreshToken
        refreshTokenDao.deleteAllByTokenKey(tokenKey);
        // 删除cookie中的tokenKey
        CookieUtils.deleteCookie(getResponse(), Constants.User.COOKICE_TOKEN_EKY);
        return ResponseResult.SUCCESS("退出登录成功.");
    }

    /**
     * 根据tokenKey获取token并解析
     *
     * @param tokenKey
     * @return
     */
    private SobUser parseByTokenKey(String tokenKey) {
        // 1. 通过tokenKey获取redis中的token
        String token = (String) redisUtils.get(Constants.User.KEY_TOKEN + tokenKey);
        // 2. 解析token
        if (token != null) {
            try {
                // 没有过期证明还在登录状态
                Claims claims = JwtUtil.parseJWT(token);
                return ClaimsUtils.claims2SobUser(claims);
            } catch (Exception e) {
                // 登录过期
                return null;
            }
        }
        return null;
    }
}
