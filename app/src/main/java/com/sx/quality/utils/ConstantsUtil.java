package com.sx.quality.utils;

import okhttp3.MediaType;

/**
 * Created by jack on 2017/10/10.
 */

public class ConstantsUtil {
    /** 基本路径 */
    //public static final String BASE_URL = "http://qyh.weidingplus.com/";
    public static final String BASE_URL = "http://qyh.apih5.com/";
    /**文件基本路径*/
    public static final String FILE_BASE_URL = "http://qyh.apih5.com/";

    /**参数格式*/
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /** 用户id */
    public static String USER_ID = "";

    /** 是否登录成功 */
    public static final String IS_LOGIN_SUCCESSFUL = "IS_LOGIN_SUCCESSFUL";

    /** 登录 */
    public static final String LOGIN = "loginApi";
    /** 上传别名 */
    public static final String SUBMIT_ALIAS = "appAddSxZlUserExtend";
    /** 承包商列表 */
    public static final String CONTRACTOR_LIST = "getAllSxZlNodeList";
    /** 获取图片列表 */
    public static final String GET_PHONE_LIST = "getPictureListByNodeId";
    /** 图片上传 */
    public static final String UP_LOAD_PHOTOS = "appUploadPicture";
    /** 审核人员 */
    public static final String GET_AUDITORS = "getSxZlCheckUserSelect";
    /** 提交审核照片 */
    public static final String SUBMIT_AUDITORS_PICTURE = "sendPictureCheckMessage";
    /** 版本检查 */
    public static final String CHECK_VERSION = "version/checkVersion";
    /** 下载APK */
    public static final String DOWNLOAD_APK = "version/downloadFile";

    /**
     * 文件存储路径
     */
    public static final String SAVE_PATH = "/mnt/sdcard/sx_quality/";
}
