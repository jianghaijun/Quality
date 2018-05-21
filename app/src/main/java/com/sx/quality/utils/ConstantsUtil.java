package com.sx.quality.utils;

import com.sx.quality.bean.SameDayBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 常量类
 * Created by jack on 2017/10/10.
 */

public class ConstantsUtil {
    /**长陵机电路径*/
    //public static final String BASE_URL = "http://jd.sxlqjt.cn:8002/";
    /**山西路桥路径*/
    public static final String BASE_URL = "http://jd.sxlqjt.cn:8002";   // http://sx.apih5.com:8088/
    //public static final String BASE_URL = "http://192.168.1.155:8080/";

    /**山西路桥传1  长陵机电传2*/
    public static String roleFlag = "1";
    /**山西路桥传2  长陵机电传2*/
    public static String roleFlag_2 = "2";
    /**山西路桥传3  长陵机电传2*/
    public static String roleFlag_3 = "21";
    /**山西路桥传  长陵机电传*/
    public static String prefix = "/apisxdehzl/";
    //public static String prefix = "";
    /**登录accountId*/
    public static String ACCOUNT_ID = "sx_qyh_deh_id";
    //public static String ACCOUNT_ID = "sx_qyh_woa_id";

    /**参数格式*/
    public static SameDayBean sameDayBean;
    public static boolean isDownloadApk = false;

    /**参数格式*/
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /** 用户id */
    public static String USER_ID = "USER_ID";
    /** 用户等级 */
    public static String USER_LEVEL = "USER_LEVEL";
    /** 用户类型 */
    public static String USER_TYPE = "USER_TYPE";
    /** 已经加载的层级 */
    public static String LEVEL_ID = "LEVEL_ID";
    /** token */
    public static String TOKEN = "TOKEN";
    /** userHead */
    public static String USER_HEAD = "USER_HEAD";
    /** 屏幕高度 */
    public static String SCREEN_HEIGHT = "SCREEN_HEIGHT";
    /** OkHttpClient */
    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30000L, TimeUnit.MILLISECONDS)
            .readTimeout(30000L, TimeUnit.MILLISECONDS)
            .build();

    /** 是否登录成功 */
    public static final String IS_LOGIN_SUCCESSFUL = "IS_LOGIN_SUCCESSFUL";

    /** 登录 */
    public static final String LOGIN = prefix + "user/" + "login";
    /** 上传别名 */
    public static final String SUBMIT_ALIAS = prefix + "appAddSxZlUserExtend";
    /** 层级列表 */
    public static final String NEW_CONTRACTOR_LIST = prefix + "getAppChildrenNodeList";
    /** 查询工序*/
    public static final String PROCESS_LIST = prefix + "getSxZlProcessList";
    /** 获取图片列表 */
    public static final String GET_PHONE_LIST = prefix + "appGetPhotoListByProcessId";
    /** 图片上传 */
    public static final String UP_LOAD_PHOTOS = prefix + "appUploadPhoto";
    /** 删除图片 */
    public static final String DELETE_PHOTOS = prefix + "batchDeleteSxZlPhoto";
    /** 审核人员 */
    public static final String GET_AUDITORS = prefix + "getSxZlCheckUserSelect";
    /** 提交审核照片 */
    public static final String SUBMIT_AUDITORS_PICTURE = prefix + "sendProcessCheckMessage";
    /** 驳回或完成 */
    public static final String REJECT_FINISH = prefix + "dehAppYesOrNoPassCheck";
    /** 检测项目 */
    public static final String GET_CHECK_LEVEL_LIST = prefix + "appGetSxZlCheckLevelList";
    /** 获取工序实测记录列表 */
    public static final String GET_PROCESS_ACTUAL_LIST = prefix + "appGetSxZlProcessActualList";
    /** 保存工序实测记录 */
    public static final String SAVE_PROCESS_ACTUAL = prefix + "batchAddSxZlProcessActual";
    /** 版本检查 */
    public static final String CHECK_VERSION = prefix + "version/checkVersion";
    /** 下载APK */
    public static final String DOWNLOAD_APK = prefix + "version/downloadFile";
    /** 获取滚动信息 */
    public static final String GET_SCROLL_INFO = prefix + "appGetNewestPhotoAndProcess";
    /** 上传用户头像 */
    public static final String UPLOAD_ICON = prefix + "appUploadIcon";
    /** 上传层厚信息 */
    public static final String UPDATE_SX_ZL_PROCESS = prefix + "updateSxZlProcess";
    /** 获取消息列表 */
    public static final String GET_TIMER_TASK_LIST = prefix + "getSxZlTimerTaskList";
    /** 获取工序详情 */
    public static final String GET_PROCESS_DETAIL = prefix + "getSxZlProcessDetail";
    /** 修改密码 */
    public static final String UPDATE_PASSWORD = prefix + "updateUserPassword";
    /** 工序报表获取首页数据 */
    public static final String PROCESS_REPORT_TODAY = prefix + "getProcessReportToday";
    /** 按分部获取当日报表详情 */
    public static final String PROCESS_PROCESS_REPORT_TODAY = prefix + "getProcessReportDetailToday";
    /** 按分部获取当日报表详情 */
    public static final String PROCESS_AND_PHOTO_LIST_TODAY = prefix + "getProcessAndPhotoListToday  ";

    /**
     * 文件存储路径
     */
    public static final String SAVE_PATH = "/mnt/sdcard/sx_quality/";
}
