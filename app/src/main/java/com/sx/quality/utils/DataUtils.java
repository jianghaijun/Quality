package com.sx.quality.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * 日期
 * 
 * @author JiangHaiJun
 * @date 2016-7-14
 */
@SuppressLint("SimpleDateFormat")
public class DataUtils {
	/**
	 * 获取系统当前日期
	 * @return
	 */
	public static String getCurrentData() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

}
