package com.sx.quality.model;

import com.sx.quality.bean.SameDayBean;

import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class SameDayDetailsModel {
    private boolean success;
    private String message;
    private int code;
    private int totalNumber;
    private List<SameDayBean> data;

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

    public List<SameDayBean> getData() {
        return data;
    }

    public void setData(List<SameDayBean> data) {
        this.data = data;
    }
}
