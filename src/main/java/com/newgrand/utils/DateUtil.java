package com.newgrand.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author: jiaxin
 * @date: 2021/9/2 13:41
 */
public class DateUtil {
    /**
     * 获取今年
     *
     * @return
     */
    public static int getYear() {
        LocalDateTime nowDateTime = new Date(System.currentTimeMillis())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return nowDateTime.getYear();
    }

    /**
     * 计算推迟n年后的日期
     *
     * @param source
     * @param years
     * @return
     */
    public static Date plusYears(Date source, int years) {
        LocalDateTime nowDateTime = source.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        nowDateTime = nowDateTime.plusYears(years);
        return Date.from(nowDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算推迟n天后的日期
     *
     * @param source
     * @param days
     * @return
     */
    public static Date plusDays(Date source, int days) {
        LocalDateTime nowDateTime = source.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        nowDateTime = nowDateTime.plusDays(days);
        return Date.from(nowDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算两个时间相差天数（后期时间 >= 早期时间）
     *
     * @param before 早期时间
     * @param after  后期时间
     * @return
     */
    public static int differentDays(Date before, Date after) {
        Calendar bCalendar = Calendar.getInstance();
        bCalendar.setTime(before);

        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(after);

        int bDay = bCalendar.get(Calendar.DAY_OF_YEAR);
        int aDay = aCalendar.get(Calendar.DAY_OF_YEAR);
        int bYear = bCalendar.get(Calendar.YEAR);
        int aYear = aCalendar.get(Calendar.YEAR);
        if (bYear != aYear) { //不同年
            int timeDistance = 0;
            for (int i = bYear; i < aYear; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { //闰年
                    timeDistance += 366;
                } else {
                    timeDistance += 365;
                }
            }
            return timeDistance + (aDay - bDay);
        } else {
            return aDay - bDay;
        }
    }

    /**
     * 获取今年有多少天
     *
     * @return
     */
    public static int getDaysOfYear() {
        return new GregorianCalendar().isLeapYear(getYear()) ? 366 : 365;
    }

    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getNowDateToString(String pattern) {
        // 创建日期时间格式化器，使用传入的模式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        // 获取当前日期时间（含时区信息）
        ZonedDateTime now = ZonedDateTime.now();
        // 格式化并返回字符串
        return now.format(formatter);
    }
}
