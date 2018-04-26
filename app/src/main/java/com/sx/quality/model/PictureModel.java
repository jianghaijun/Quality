package com.sx.quality.model;

import com.sx.quality.bean.PictureBean;

import java.util.List;

/**
 * Create dell By 2017/11/10 16:41
 */

public class PictureModel {
    private String selectUserId;
    private String rootLevelId;
    private String processId;
    private List<PictureBean> sxZlPhotoList;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getSelectUserId() {
        return selectUserId;
    }

    public void setSelectUserId(String selectUserId) {
        this.selectUserId = selectUserId;
    }

    public String getRootLevelId() {
        return rootLevelId;
    }

    public void setRootLevelId(String rootLevelId) {
        this.rootLevelId = rootLevelId;
    }

    public List<PictureBean> getSxZlPhotoList() {
        return sxZlPhotoList;
    }

    public void setSxZlPhotoList(List<PictureBean> sxZlPhotoList) {
        this.sxZlPhotoList = sxZlPhotoList;
    }
}
