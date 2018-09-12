package com.bstek.cola.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

	/**
	 * 获取当天时间  Sat Apr 21 00:00:00 CST 2018
	 * @return
	 */
	public static Date getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 根据参数获取当天间隔的天数的时间
	 * @return
	 */
	public static Date getToday(int increment) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, increment);
		return calendar.getTime();
	}
	
	/**
	 * 根据参数获取  参数date 间隔的天数 increment 的时间
	 * @return
	 */
	public static Date getDate(Date date, int increment) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, increment);
		return calendar.getTime();
	}
	
	/**
	 * 根据参数获取  参数date 间隔的分钟数 minute 的时间
	 * @return
	 */
	public static Date getDateIntervalMinutes(Date date, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		long millis = calendar.getTimeInMillis() + minute * 60 * 1000;
		return new Date(millis);
	}
	
	/**
	 * 根据参数获取  参数date 间隔的月份 increment 的时间
	 * @return
	 */
	public static Date getDateIntervalMonth(Date date, int increment) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.MONTH, increment);
		return calendar.getTime();
	}
	
	/**
	 * 返回清空时、分、秒的 date
	 * @param date
	 * @return
	 */
	public static Date clearTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取date开始时间   Sat Apr 21 00:00:00 CST 2018
	 * @param date
	 * @return
	 */
	public static Date startTime(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取date结束时间   Sat Apr 21 23:59:59 CST 2018
	 * @param date
	 * @return
	 */
	public static Date endTime(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * 将 Date 按 patten 格式转换成 String  "yyyy-MM-dd HH:mm:ss"
	 * @param date
	 * @param patten
	 * @return
	 */
	public static String dateToString(Date date, String patten) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(patten, Locale.CHINA);
		return dateFormat.format(date);
	}

	/**
	 * 将 String 按 patten 格式转换成 Date  "yyyy-MM-dd HH:mm:ss"
	 * @param date
	 * @param patten
	 * @return
	 */
	public static Date stringToDate(String source, String patten) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(patten, Locale.CHINA);
		Date date = null;
		try {
			date = dateFormat.parse(source);
		} catch (ParseException e) {
			
		}
		return date;
	}
	
	/**
	 * 判断 firstDate 是否 大于等于 lastDate, 大于等于 返回 true, 反之 false
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static boolean ne(Date firstDate, Date lastDate) {
		if (!firstDate.before(lastDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断 firstDate 是否 大于 lastDate, 大于等于 返回 true, 反之 false
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static boolean gt(Date firstDate, Date lastDate) {
		if (firstDate.after(lastDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断 firstDate 是否 小于等于 lastDate, 小于等于 返回 true, 反之 false
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static boolean le(Date firstDate, Date lastDate) {
		if (!firstDate.after(lastDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断 firstDate 是否 小于 lastDate, 小于等于 返回 true, 反之 false
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static boolean lt(Date firstDate, Date lastDate) {
		if (firstDate.before(lastDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断 firstDate 是否等于 lastDate
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static boolean eq(Date firstDate, Date lastDate) {
		if (org.apache.commons.lang.time.DateUtils.isSameDay(firstDate, lastDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断 firstDate 与 lastDate 间隔的天数
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static long intervalDay(Date firstDate, Date lastDate) {
		return (firstDate.getTime() - lastDate.getTime()) / (1000 * 3600 * 24);
	}
	
	/**
	 * 判断 firstDate 与 lastDate 间隔分钟数
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static long intervalMinute(Date firstDate, Date lastDate) {
		return (firstDate.getTime() - lastDate.getTime()) / (1000 * 60);
	}
	
	/**
	 * 判断 year 与 month 判断 month 的天数
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDay(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		return calendar.getActualMaximum(Calendar.DATE);
	}
	
	/**
	 * 获取date 间隔 minute 之后的时间
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date getDateByintervalMinute(Date date, int minute){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}
	
	/**
	 * 获取date 间隔 hour 之后的时间
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date getDateByintervalHour(Date date, int hour){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, hour);
		return calendar.getTime();
	}

}
