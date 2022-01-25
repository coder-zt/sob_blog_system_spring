package com.zhangtao.blog.interceptor;

import com.google.gson.Gson;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.CookieUtils;
import com.zhangtao.blog.utils.RedisUtils;
import com.zhangtao.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    RedisUtils redisUtils;

    @Autowired
    Gson gson;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            CheckTooFrequentCommit methodAnnotation = handlerMethod.getMethodAnnotation(CheckTooFrequentCommit.class);
            if (methodAnnotation != null) {
                String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
                String methodName = handlerMethod.getMethod().getName();
                if (!TextUtils.isEmpty(tokenKey)) {
                    String hasCommit =
                        (String)redisUtils.get(Constants.User.KEY_COMMIT_TOKEN_RECORD + tokenKey + methodName);
                    if(!TextUtils.isEmpty(hasCommit)){
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json");
                        ResponseResult responseResult = ResponseResult.FAILED("提交过于频繁，请稍后重试.");
                        PrintWriter writer = response.getWriter();
                        writer.write(gson.toJson(responseResult));
                        writer.flush();
                        return false;
                    }else{
                        redisUtils.set(Constants.User.KEY_COMMIT_TOKEN_RECORD + tokenKey + methodName, "true",
                                Constants.TimeValueInSecond.SECOND_10);
                    }
                }
            }
        }
        //true 表示放行
        // false 表示拦截
        return true;
    }
}
