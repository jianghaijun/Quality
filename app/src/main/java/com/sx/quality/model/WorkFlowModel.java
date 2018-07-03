package com.sx.quality.model;

import com.sx.quality.bean.WorkFlowBean;

/**
 * Create dell By 2018/6/13 11:32
 */

public class WorkFlowModel {
    private String flowId;
    private String workId;
    private String reviewNodeId;
    private String reviewUserObject;
    private String mainTableName;
    private String mainTablePrimaryId;
    private String mainTablePrimaryIdName;
    private String buttonId;
    private String title;
    private String fileOperationFlag;
    private String opinionContent;
    private WorkFlowBean mainTableObject;
    /*private String opinionContent;
    private String opinionContent;*/

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getReviewNodeId() {
        return reviewNodeId;
    }

    public void setReviewNodeId(String reviewNodeId) {
        this.reviewNodeId = reviewNodeId;
    }

    public String getReviewUserObject() {
        return reviewUserObject;
    }

    public void setReviewUserObject(String reviewUserObject) {
        this.reviewUserObject = reviewUserObject;
    }

    public String getMainTableName() {
        return mainTableName;
    }

    public void setMainTableName(String mainTableName) {
        this.mainTableName = mainTableName;
    }

    public String getMainTablePrimaryId() {
        return mainTablePrimaryId;
    }

    public void setMainTablePrimaryId(String mainTablePrimaryId) {
        this.mainTablePrimaryId = mainTablePrimaryId;
    }

    public String getMainTablePrimaryIdName() {
        return mainTablePrimaryIdName;
    }

    public void setMainTablePrimaryIdName(String mainTablePrimaryIdName) {
        this.mainTablePrimaryIdName = mainTablePrimaryIdName;
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileOperationFlag() {
        return fileOperationFlag;
    }

    public void setFileOperationFlag(String fileOperationFlag) {
        this.fileOperationFlag = fileOperationFlag;
    }

    public String getOpinionContent() {
        return opinionContent;
    }

    public void setOpinionContent(String opinionContent) {
        this.opinionContent = opinionContent;
    }

    public WorkFlowBean getMainTableObject() {
        return mainTableObject;
    }

    public void setMainTableObject(WorkFlowBean mainTableObject) {
        this.mainTableObject = mainTableObject;
    }
}
