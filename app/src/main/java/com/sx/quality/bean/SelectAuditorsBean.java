package com.sx.quality.bean;

/**
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 * O\  =  /O
 * ____/`---'\____
 * .'  \\|     |//  `.
 * /  \\|||  :  |||//  \
 * /  _||||| -:- |||||-  \
 * |   | \\\  -  /// |   |
 * | \_|  ''\---/''  |   |
 * \  .-\__  `-`  ___/-. /
 * ___`. .'  /--.--\  `. . __
 * ."" '<  `.___\_<|>_/___.'  >'"".
 * | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 * \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 佛祖保佑       永无BUG
 * Created by HaiJun on 2017/11/9 17:29
 */

public class SelectAuditorsBean {
    private String userId;
    private String realName;
    private String checkLevelId;
    private String checkLevelName;
    private boolean isSelect = false;

    public String getCheckLevelId() {
        return checkLevelId;
    }

    public void setCheckLevelId(String checkLevelId) {
        this.checkLevelId = checkLevelId;
    }

    public String getCheckLevelName() {
        return checkLevelName;
    }

    public void setCheckLevelName(String checkLevelName) {
        this.checkLevelName = checkLevelName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
