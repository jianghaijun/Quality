package com.sx.quality.model;

import com.sx.quality.bean.ContractorListBean;
import com.sx.quality.bean.NewContractorListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class ContractorListModel {
    private boolean success;
    private String message;
    private int errcode;
    private List<NewContractorListBean> data;
    //private List<ContractorListBean> data;

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

    public List<NewContractorListBean> getData() {
        return data == null || data.equals("null") ? new ArrayList<NewContractorListBean>() : data;
    }

    public void setData(List<NewContractorListBean> data) {
        this.data = data;
    }
}
