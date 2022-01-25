package com.zhangtao.blog.services.impl;

import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.CookieUtils;
import com.zhangtao.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("permission")
public class PermissionService {

    @Autowired
    private IUserService userService;

    public boolean adminPermission() {
        // 获取到当前权限所有的角色，进行角色对比即可确定权限
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //如果token返回false
        String token = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
        if (TextUtils.isEmpty(token)) {
            return false;
        }
        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null || TextUtils.isEmpty(sobUser.getRoles())) {
            return false;
        }
        if (Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())) {
            return true;
        }
        return false;
    }
}
