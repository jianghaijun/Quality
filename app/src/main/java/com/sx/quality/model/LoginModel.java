package com.sx.quality.model;

import com.sx.quality.bean.LoginBean;

/**
 * Created by jack on 2017/10/11.
 */

public class LoginModel {
    private boolean success;
    private String message;
    private int errcode;
    private LoginBean data;

    public LoginBean getData() {
        return data;
    }

    public void setData(LoginBean data) {
        this.data = data;
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

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }
}
