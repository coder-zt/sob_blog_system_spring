package com.zhangtao.blog.config;

import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ErrorPageRegistrar;
import org.springframework.boot.web.servlet.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorCodeConfig implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
        errorPageRegistry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/403"),
                new ErrorPage(HttpStatus.NOT_FOUND, "/404"),
                new ErrorPage(HttpStatus.GATEWAY_TIMEOUT, "/504"),
                new ErrorPage(HttpStatus.HTTP_VERSION_NOT_SUPPORTED, "/5O5"));
    }
}
