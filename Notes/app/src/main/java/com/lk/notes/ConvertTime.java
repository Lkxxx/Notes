package com.lk.notes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lk on 07/05.
 */
public class ConvertTime {
    public static String convertTime(String time, String str) {
        long id = Long.parseLong(str.substring(0, 10));
        String outputTime = "未知";
        int month = Integer.parseInt(time.substring(0, 2));
        int day = Integer.parseInt(time.substring(3, 5));
        int hour = Integer.parseInt(time.substring(6, 8));
        int minute = Integer.parseInt(time.substring(9, 11));

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        int yearnow = c.get(Calendar.YEAR);
        int monthnow = c.get(Calendar.MONTH)+1;
        int daynow = c.get(Calendar.DAY_OF_MONTH);
        int hournow = c.get(Calendar.HOUR_OF_DAY);
        int minutenow = c.get(Calendar.MINUTE);
        if (id - 31556926 * (yearnow - 1970) > 0 && id - 31556926 * (yearnow - 1970) <= 31556926) {
            if (month == monthnow) {
                if (daynow - day == 2) {
                    outputTime = "前天" ;
                } else if (daynow - day == 1) {
                    outputTime = "昨天" + hour + "时";
                } else if (daynow == day) {
                    if (hournow - hour <= 1) {
                        if (minutenow - minute == 0) {
                            outputTime = "刚刚";
                        } else if (hournow - hour == 1 && minutenow - minute < 0) {
                            outputTime = (60 + minutenow - minute) + "分钟前";
                        } else {
                            outputTime = (minutenow - minute) + "分钟前";
                        }
                    } else {
                        outputTime = (hournow - hour) + "小时前";
                    }
                } else {
                    outputTime = day + "日" + hour + "时";
                }
            } else {
                outputTime = month + "月" + day + "日";
            }
        } else if (id - 31556926 * (yearnow - 1970) > 31556926 && id - 31556926 * (yearnow - 1970) <= 31556926) {
            outputTime = "去年" + month + "月" + day + "日";
        } else if (id - 31556926 * (yearnow - 1970) > 31556926) {
            outputTime = "前年" + month + "月" + day + "日";
        } else {
            outputTime = "3年前";
        }

        return outputTime;
    }

    public static String convertClock(String str) {
        String outputTime = "未知";
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        Date d = null;
        try {
            d = formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeGetTime = d.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                Locale.getDefault());
        String str_time = sdf.format(timeGetTime);
        int clock[] = new int[]{Integer.parseInt(str_time.substring(0, 4)),
                Integer.parseInt(str_time.substring(5, 7)),
                Integer.parseInt(str_time.substring(8, 10)),
                Integer.parseInt(str_time.substring(11, 13)),
                Integer.parseInt(str_time.substring(14, 16))};
        int now[] = {c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)};
        c.add(Calendar.MONTH, 1);
        if (clock[0] == now[0]) {
            if (clock[1] == c.get(Calendar.MONTH)) {
                if (clock[2] == c.get(Calendar.DAY_OF_MONTH)) {
                    outputTime = "今天" + clock[3] + ":" + clock[4];
                } else {
                    c.setTimeInMillis(System.currentTimeMillis());
                    c.add(Calendar.DATE, 1);
                    if (clock[2] == c.get(Calendar.DAY_OF_MONTH)) {
                        outputTime = "明天" + clock[3] + ":" + clock[4];
                    } else {
                        outputTime = clock[1] + "月" + clock[2] + "日" + clock[3] + ":" + clock[4];
                    }
                }
            }else {
                outputTime = clock[1] + "月" + clock[2] + "日" + clock[3] + ":" + clock[4];
            }
        } else {
            outputTime = clock[0] + "年" + clock[1] + "月" + clock[2] + "日";
        }


        return outputTime;
    }
}
