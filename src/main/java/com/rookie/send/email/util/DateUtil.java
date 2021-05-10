package com.rookie.send.email.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author rookie
 * @Date 2021/5/10 16:42
 * @Description
 **/
public class DateUtil {

    public static boolean belongCalendar(Date nowTime, Date beginTime,
                                         Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public static Date getNowTime(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
        Date now = null;
        try {
            now = df.parse(df.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    public static Date getBeginTime(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
        Date beginTime = null;
        try {
            beginTime = df.parse("23:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beginTime;
    }

    public static Date getEndTime(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
        Date endTime = null;
        try {
            endTime = df.parse("24:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endTime;
    }
}
