package com.sx.quality.model;

import com.sx.quality.bean.WorkingBean;

import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class WorkingModel {
    private boolean success;
    private String message;
    private int code;
    private List<WorkingBean> data;

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<WorkingBean> getData() {
        return data;
    }

    public void setData(List<WorkingBean> data) {
        this.data = data;
    }
}
