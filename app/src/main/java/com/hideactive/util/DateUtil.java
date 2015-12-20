package com.hideactive.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Senierr on 2015/12/19.
 */
public class DateUtil {

    public static Date string2Date(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String date2String(Date date, String formatStr) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static String getDiffTime(Date lastDate) {
        if (lastDate == null) {
            return null;
        }
        long currentMiles = System.currentTimeMillis();
        long lastMiles = lastDate.getTime();
        long diff = currentMiles - lastMiles;
        // 小于1秒
        if (diff <= 1000) {
            return "刚刚";
        } else if (diff <= 60000) {
            // 大于1秒小于60秒
            return (diff/1000) + "秒前";
        } else if (diff <= (1000 * 60 * 60)) {
            // 大于60秒(1分钟)小于60分钟（1小时）
            return (diff/1000/60) + "分钟前";
        } else if (diff <= (1000 * 60 * 60 * 24)) {
            // 大于60分钟（1小时）小于24小时
            return (diff/1000/60/60) + "小时前";
        } else {
            // 大于24小时
            return date2String(lastDate, "MM-dd");
        }
    }
}
