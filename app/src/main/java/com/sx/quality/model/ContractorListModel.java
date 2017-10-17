package com.sx.quality.model;

import com.sx.quality.bean.ContractorListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class ContractorListModel {
    private boolean success;
    private String message;
    private int errcode;
    private List<ContractorListBean> data;

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

    public List<ContractorListBean> getData() {
        return data == null || data.equals("null") ? new ArrayList<ContractorListBean>() : data;
    }

    public void setData(List<ContractorListBean> data) {
        this.data = data;
    }
}
