package com.sx.quality.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class NewContractorListBean extends DataSupport implements Serializable {
    private String nodeId;
    private String nodeName;
    private String nodeTitle;
    private String folderFlag;
    private String pid;
    // 该工序下有多少道工序
    private int processNum;
    // 已完成多少道工序
    private int finishedNum;
    private String isFinish;
    private long outTime;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFolderFlag() {
        return folderFlag;
    }

    public void setFolderFlag(String folderFlag) {
        this.folderFlag = folderFlag;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    public String getNodeId() {
        return nodeId == null || nodeId.equals("null") ? "" : nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName == null || nodeName.equals("null") ? "" : nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getFinishedNum() {
        return finishedNum;
    }

    public void setFinishedNum(int finishedNum) {
        this.finishedNum = finishedNum;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }
}
