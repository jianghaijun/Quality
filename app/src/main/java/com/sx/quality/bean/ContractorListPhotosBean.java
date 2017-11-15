package com.sx.quality.bean;

import com.sx.quality.utils.DataUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by jack on 2017/10/11.
 */

public class ContractorListPhotosBean extends DataSupport implements Serializable {
    private int isToBeUpLoad = -1;
    private String pictureId;
    private String pictureDesc;
    private String thumbPath;
    private String createtime;
    private String nodeId;
    private String pictureName;
    private String pictureNameNoSuffix;
    private String pictureAddress;
    private boolean isCanSelect = false;
    private String checkFlag;

    public String getCheckFlag() {
        return checkFlag == null || "".equals(checkFlag) ? "0" : checkFlag;
    }

    public void setCheckFlag(String checkFlag) {
        this.checkFlag = checkFlag;
    }

    public boolean isCanSelect() {
        return isCanSelect;
    }

    public void setCanSelect(boolean canSelect) {
        isCanSelect = canSelect;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getPictureNameNoSuffix() {
        return pictureNameNoSuffix;
    }

    public void setPictureNameNoSuffix(String pictureNameNoSuffix) {
        this.pictureNameNoSuffix = pictureNameNoSuffix;
    }

    public String getCreatetime() {
        return createtime == null || createtime.equals("") ? DataUtils.getCurrentData() : createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getIsToBeUpLoad() {
        return isToBeUpLoad;
    }

    public void setIsToBeUpLoad(int isToBeUpLoad) {
        this.isToBeUpLoad = isToBeUpLoad;
    }

    public String getPictureDesc() {
        return pictureDesc;
    }

    public void setPictureDesc(String pictureDesc) {
        this.pictureDesc = pictureDesc;
    }

    public String getPictureId() {
        return pictureId == null || pictureId.equals("null") ? "" : pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getNodeId() {
        return nodeId == null || nodeId.equals("null") ? "" : nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getPictureName() {
        return pictureName == null || pictureName.equals("null") ? "" : pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureAddress() {
        return pictureAddress == null || pictureAddress.equals("null") ? "" : pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }
}
