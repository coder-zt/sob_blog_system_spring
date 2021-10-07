package com.zhangtao.blog.responese;

public class ResponseResult {

    int code;
    boolean success;
    String message;
    Object data;

    public ResponseResult(ResponseState responseState) {
        this.code = responseState.code;
        this.success = responseState.success;
        this.message = responseState.message;
    }

    public static ResponseResult SUCCESS(){
        return new ResponseResult(ResponseState.SUCCESS);
    }

    public static ResponseResult GET(ResponseState state){
        return new ResponseResult(state);
    }

    public static ResponseResult SUCCESS(String message){
        ResponseResult responseResult = new ResponseResult(ResponseState.SUCCESS);
        responseResult.setMessage(message);
        return responseResult;
    }

    public static ResponseResult FAILED(){
        return new ResponseResult(ResponseState.FAILED);
    }
    public static ResponseResult FAILED(String message){
        ResponseResult responseResult = new ResponseResult(ResponseState.FAILED);
        responseResult.setMessage(message);
        return responseResult;
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

    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }
}
