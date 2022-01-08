package com.hjq.demo.http.response;

public class ReqErr {
    /**
     * success : false
     * code : 500
     * message : null
     * messageKey : Authenticate
     * data : null
     * exceptionClazz : null
     */

    private boolean success;
    private int code;
    private String message;
    private String messageKey;
    private String data;
    private String exceptionClazz;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExceptionClazz() {
        return exceptionClazz;
    }

    public void setExceptionClazz(String exceptionClazz) {
        this.exceptionClazz = exceptionClazz;
    }
    /**
     * code : 401
     * msg : 请登录后操作
     * time : 1576547893
     * data : null
     */


}
