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
    private String pushMessage;
    private String dismissal;
    private String stateFlag;
    private String recordType;
    private List<PictureBean> sxZlPhotoList;

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public String getStateFlag() {
        return stateFlag;
    }

    public void setStateFlag(String stateFlag) {
        this.stateFlag = stateFlag;
    }

    public String getDismissal() {
        return dismissal;
    }

    public void setDismissal(String dismissal) {
        this.dismissal = dismissal;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

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
