package com.sx.quality.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by jack on 2017/10/11.
 */

public class QualityInspectionBean extends DataSupport implements Serializable {
    private String userExtendId;
    private String realName;
    private String rootLevelId;

    public String getUserExtendId() {
        return userExtendId;
    }

    public void setUserExtendId(String userExtendId) {
        this.userExtendId = userExtendId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRootLevelId() {
        return rootLevelId;
    }

    public void setRootLevelId(String rootLevelId) {
        this.rootLevelId = rootLevelId;
    }
}
