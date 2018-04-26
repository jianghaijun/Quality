package com.sx.quality.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * Create dell By 2018/4/8 15:30
 */

public class WorkingBean extends DataSupport implements Serializable {
    private String processId;        // 工序ID
    private String processName;      // 工序名称
    private String processCode;      // 编码
    private String photoContent;     // 拍照内容
    private String photoDistance;    // 距离及角度
    private String photoNumber;      // 拍照最少张数
    private String longitude;        // 经度
    private String latitude;         // 纬度
    private String location;         // 地理位置
    private String levelNameAll;     // 工序部位
    private String levelId;          // 工序部位
    private long enterTime;          // 录入时间
    private String actualNumber;     // 实际照片数量
    private String checkNameAll;     // 确认者姓名all
    private String createUser;       // 拍照者
    private String processState;     // 工序状态

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getPhotoContent() {
        return photoContent;
    }

    public void setPhotoContent(String photoContent) {
        this.photoContent = photoContent;
    }

    public String getPhotoDistance() {
        return photoDistance;
    }

    public void setPhotoDistance(String photoDistance) {
        this.photoDistance = photoDistance;
    }

    public String getPhotoNumber() {
        return photoNumber;
    }

    public void setPhotoNumber(String photoNumber) {
        this.photoNumber = photoNumber;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLevelNameAll() {
        return levelNameAll;
    }

    public void setLevelNameAll(String levelNameAll) {
        this.levelNameAll = levelNameAll;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public String getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(String actualNumber) {
        this.actualNumber = actualNumber;
    }

    public String getCheckNameAll() {
        return checkNameAll;
    }

    public void setCheckNameAll(String checkNameAll) {
        this.checkNameAll = checkNameAll;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getProcessState() {
        return processState;
    }

    public void setProcessState(String processState) {
        this.processState = processState;
    }
}
