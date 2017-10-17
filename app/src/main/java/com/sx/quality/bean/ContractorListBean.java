package com.sx.quality.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class ContractorListBean {
    private String nodeId;
    private String nodeName;
    private List<ContractorListBean> sxZlNodeList;

    public String getNodeId() {
        return nodeId == null || nodeId.equals("null")  ? "" : nodeId;
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

    public List<ContractorListBean> getSxZlNodeList() {
        return sxZlNodeList == null || sxZlNodeList.equals("null") ? new ArrayList<ContractorListBean>() : sxZlNodeList;
    }

    public void setSxZlNodeList(List<ContractorListBean> sxZlNodeList) {
        this.sxZlNodeList = sxZlNodeList;
    }
}
