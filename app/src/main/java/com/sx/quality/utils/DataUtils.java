package com.sx.quality.utils;

import java.text.ParseException;
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

	/**
	 * 将日期转换字符
	 * @return
	 */
	public static String getDataToStr(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (date != null) {
			return formatter.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 将字符转换日期
	 * @return
	 */
	public static Date getStrToData(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateString = null;
		try {
			dateString = formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateString;
	}

}
