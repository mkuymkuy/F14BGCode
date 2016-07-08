/*
 * Created on 2004-12-19
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.f14.framework.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static Calendar cal = Calendar.getInstance();
	
	/**
	 * 获取"yyyy-MM-dd HH:mm:ss"格式的系统当前时间
	 ** @return
	 */
	public static String getNowDateByFormat1() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String dt1 = format1.format(date);
		 
		return dt1;
	}

	/**
	 *  获取"yyyyMMddHHmmss"格式的系统当前时间
	 * @return
	 */
	public static String getNowDateByFormat2() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String dt1 = format1.format(date);
		return dt1;
	}
	
	/**
	 *  获取"yyyy-MM-dd"格式的系统当前时间
	 * @return
	 */
	public static String getNowDateByFormat3() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dt1 = format1.format(date);
		return dt1;
	}
	
	/**
	 *  转化 日期 TO String 类型, 格式"yyyy-MM-dd"。
	 * @return
	 */
	public static String parseDate(Date date) {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");		
		String dt1 = format1.format(date);
		return dt1;
	}

	/**
	 * 比较日期大小 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean compareDate(int[] date1, int[] date2) {
		int countEqual = 0;
		for (int i = 0; i < date1.length; i++) {
			if (date1[i] < date2[i]) {
				return true;
			} else if (date1[i] == date2[i]) {
				countEqual++;
			} else {
				return false;
			}
		}
		if (countEqual == date1.length)
			return true;
		return false;
	}

	/**
	 * 一个公共的处理方法，将字符串的日期转换成日期对象 日期格式由参数format指定
	 * 
	 */
	public static Date parseDate(String stringDate, String format) {
		if ((stringDate == null) || stringDate.trim().equals("")) {
			return null;
		}
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
		try {
			return sdf.parse(stringDate);
		} catch (ParseException ex) {
			// logger.info("parseExcpetion: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 一个公共的处理方法，将字符串的日期转换成日期对象 日期格式输入为yyyy-MM-dd，输出为yyyy-MM-dd 00:00:00的时间对象
	 * 
	 */
	public static Date parseDateFirstTime(String stringDate) {
		if ((stringDate == null) || stringDate.trim().equals("")) {
			return null;
		}
		stringDate +=" 00:00:00";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(stringDate);
		} catch (ParseException ex) {
			// logger.info("parseExcpetion: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 一个公共的处理方法，将字符串的日期转换成日期对象 日期格式输入为yyyy-MM-dd，输出为yyyy-MM-dd 23:59:59的时间对象
	 * 
	 */
	public static Date parseDateEndTime(String stringDate) {
		if ((stringDate == null) || stringDate.trim().equals("")) {
			return null;
		}
		stringDate +=" 23:59:59";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(stringDate);
		} catch (ParseException ex) {
			// logger.info("parseExcpetion: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}
	/**
	 * @return 当前系统日期
	 
	public static Date getSysDate() {
		Date sysDate = null;
		java.text.SimpleDateFormat a = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		java.text.SimpleDateFormat b = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			sysDate = b.parse(a.format(new Date()));
		} catch (java.text.ParseException ex) {
			ex.getMessage();
		}
		return sysDate;
	}
	*/
	
	/**
	 * 一个公共的处理方法，将日期转换成字符串 字符串格式由参数format指定
	 * 
	 */
	public static String parseDate(Date date, String format) {
		if (date == null) {
			return null;
		}
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	/**
	 * 取得日期后几天的日期,offset为天数偏移量
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getLaterDate(Date date, int offset){
		cal.setTime(date);
		cal.add(Calendar.DATE, offset);
		return cal.getTime();
	}
}
