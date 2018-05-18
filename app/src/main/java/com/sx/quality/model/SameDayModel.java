package com.sx.quality.model;

import com.sx.quality.bean.SameDayBean;
import com.sx.quality.bean.WorkingBean;

import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class SameDayModel {
    private boolean success;
    private String message;
    private int code;
    private int totalNumber;
    private SameDayBean data;

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SameDayBean getData() {
        return data;
    }

    public void setData(SameDayBean data) {
        this.data = data;
    }
}
