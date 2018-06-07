package com.sx.quality.model;

import com.sx.quality.bean.WorkingBean;

/**
 * Create dell By 2018/6/7 13:42
 */

public class ProcessDetailsModel {
    private boolean success;
    private String message;
    private String code;
    private WorkingBean data;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public WorkingBean getData() {
        return data;
    }

    public void setData(WorkingBean data) {
        this.data = data;
    }
}
