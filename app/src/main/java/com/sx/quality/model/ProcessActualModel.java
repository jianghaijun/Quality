package com.sx.quality.model;

import com.sx.quality.bean.MeasuredRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class ProcessActualModel {
    private boolean success;
    private String message;
    private int code;
    private List<MeasuredRecordBean> data;

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

    public List<MeasuredRecordBean> getData() {
        return data == null || data.equals("null") ? new ArrayList<MeasuredRecordBean>() : data;
    }

    public void setData(List<MeasuredRecordBean> data) {
        this.data = data;
    }
}
