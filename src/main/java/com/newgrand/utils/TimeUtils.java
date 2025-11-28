package com.newgrand.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhaofengjie
 * @className TimeUtils
 * @description:
 * @date 2022/3/24 11:09
 */

public class TimeUtils {
    private static final String TIME_PATTERN_1 = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_PATTERN_2 = "yyyy-MM-dd HH:mm:ss";

    private static final String TIME_PATTERN_3 = "yyyy-MM-dd";

    private static final String TIME_PATTERN_4 = "yyMMdd";
    private static final String YEAR = "yyyy";
    private static final String MOTH = "M";


    public static String dateToStr(Date date) {
        DateFormat df1 = new SimpleDateFormat(TIME_PATTERN_1);
        return df1.format(date);
    }

    public static String getDateStr() {
        DateFormat df1 = new SimpleDateFormat(TIME_PATTERN_1);
        return df1.format(new Date());
    }

    public static Date getDate() {
        return new Date();
    }

    public static String getDateStr(String forStr) {
        DateFormat df1 = new SimpleDateFormat(forStr);
        return df1.format(new Date());
    }

    public static String strTodate(String str) {
        DateFormat df1 = new SimpleDateFormat(TIME_PATTERN_1);
        return df1.format(str);
    }

    public static Date strPdate(String str) throws ParseException {
        DateFormat df1 = new SimpleDateFormat(TIME_PATTERN_1);
        return df1.parse(str);
    }

    public static String getDate(Date date) {
        DateFormat df1 = new SimpleDateFormat(TIME_PATTERN_2);
        return df1.format(date);
    }

    public static String getYear(Date date) {
        DateFormat df1 = new SimpleDateFormat(YEAR);
        return df1.format(date);
    }

    public static String getMonth(Date date) {
        DateFormat df1 = new SimpleDateFormat(MOTH);
        return df1.format(date);
    }

    public static String getDay(Date date) {
        DateFormat df1 = new SimpleDateFormat(TIME_PATTERN_3);
        return df1.format(date);
    }

    public static Date parseStringToDate(String date) throws ParseException {
        Date result = null;
        String parse = date;
        parse = parse.replaceFirst("^[0-9]{4}([^0-9]?)", "yyyy$1");
        parse = parse.replaceFirst("^[0-9]{2}([^0-9]?)", "yy$1");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1MM$2");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}( ?)", "$1dd$2");
        parse = parse.replaceFirst("( )[0-9]{1,2}([^0-9]?)", "$1HH$2");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1mm$2");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1ss$2");
        DateFormat format = new SimpleDateFormat(parse);
        result = format.parse(date);
        return result;
    }

    /**
     * 获取当天0点0分0秒（00:00:00）
     *
     * @return
     */
    public static String getTimesmorning() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -1);
        long todayZero = calendar.getTimeInMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(todayZero);
        Date date = new Date(lt);
        return simpleDateFormat.format(date);
    }
}
