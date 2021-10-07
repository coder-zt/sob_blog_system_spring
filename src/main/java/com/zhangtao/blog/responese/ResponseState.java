package com.zhangtao.blog.responese;

public enum ResponseState {
    SUCCESS(2000, true, "操作成功"),
    FAILED(4000, false, "操作失败");

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
