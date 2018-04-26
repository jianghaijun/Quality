package com.sx.quality.utils;

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
    public static final String BASE_URL = "http://114.116.12.219:8007";   // http://sx.apih5.com:8088/
    //public static final String BASE_URL = "http://192.168.1.110:8080/";

    /**山西路桥传1  长陵机电传2*/
    public static String roleFlag = "1";
    /**山西路桥传  长陵机电传*/
    public static String prefix = "/sxdehzl/";
    //public static String prefix = "";
    /**登录accountId*/
    public static String ACCOUNT_ID = "sx_qyh_deh_id";
    //public static String ACCOUNT_ID = "sx_qyh_woa_id";

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
    public static final String SUBMIT_AUDITORS_PICTURE = prefix + "sendPhotoCheckMessage";
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

    /**
     * 文件存储路径
     */
    public static final String SAVE_PATH = "/mnt/sdcard/sx_quality/";
}
