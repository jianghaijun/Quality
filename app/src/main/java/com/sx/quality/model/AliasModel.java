package com.sx.quality.model;

import com.sx.quality.bean.AliasBean;

/**
 * Created by jack on 2017/10/11.
 */

public class AliasModel {
    private boolean success;
    private String message;
    private int code;
    private String pictureId;
    private AliasBean data;

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

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public AliasBean getData() {
        return data;
    }

    public void setData(AliasBean data) {
        this.data = data;
    }
}
