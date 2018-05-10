package com.sx.quality.bean;

import java.util.List;

/**
 * Create dell By 2018/4/10 16:49
 */

public class AliasBean {
    private String userType;
    private String roleFlag;
    private List<QualityInspectionBean> sxZlUserExtendList;

    public List<QualityInspectionBean> getSxZlUserExtendList() {
        return sxZlUserExtendList;
    }

    public void setSxZlUserExtendList(List<QualityInspectionBean> sxZlUserExtendList) {
        this.sxZlUserExtendList = sxZlUserExtendList;
    }

    public String getRoleFlag() {
        return roleFlag;
    }

    public void setRoleFlag(String roleFlag) {
        this.roleFlag = roleFlag;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
