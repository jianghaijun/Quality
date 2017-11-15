package com.sx.quality.model;

import com.sx.quality.bean.PictureBean;

import java.util.List;

/**
 * Create dell By 2017/11/10 16:41
 */

public class PictureModel {
    private String selectUserId;
    private List<PictureBean> sxZlPictureList;

    public String getSelectUserId() {
        return selectUserId;
    }

    public void setSelectUserId(String selectUserId) {
        this.selectUserId = selectUserId;
    }

    public List<PictureBean> getSxZlPictureList() {
        return sxZlPictureList;
    }

    public void setSxZlPictureList(List<PictureBean> sxZlPictureList) {
        this.sxZlPictureList = sxZlPictureList;
    }
}
