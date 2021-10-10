package com.zhangtao.blog.responese;

public enum ResponseState {
    SUCCESS(2000, true, "操作成功"),
    REGISTER_SUCCESS(2001, true, "注册成功"),
    FAILED(4000, false, "操作失败"),
    ACCOUNT_NOT_LOGIN(4002, false, "账号未登录"),
    PERMISSION_FORBID(4003, false, "无权限访问"),
    ACCOUNT_DENIED(4004, false, "账号已被封禁"),
    ERROR_403(4005, false, "无权限访问"),
    ERROR_404(4006, false, "页面丢失"),
    ERROR_504(4007, false, "系统繁忙，请稍后重试"),
    ERROR_505(4008, false, "访问方法错误"),
    ;

    int code;
    boolean success;
    String message;

    ResponseState(int code, boolean success, String message) {
        this.code = code;
        this.success = success;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
