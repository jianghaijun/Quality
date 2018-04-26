package com.sx.quality.model;

import com.sx.quality.bean.NewContractorListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class ContractorListModel {
    private boolean success;
    private String message;
    private int code;
    private List<NewContractorListBean> data;

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

    public List<NewContractorListBean> getData() {
        return data;
    }

    public void setData(List<NewContractorListBean> data) {
        this.data = data;
    }
}
