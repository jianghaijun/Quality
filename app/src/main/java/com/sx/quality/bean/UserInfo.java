package com.sx.quality.bean;

/**
 * Create dell By 2018/4/10 16:31
 */

public class UserInfo {
    private String realName;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName == null ? "" : realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
