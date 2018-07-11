package cn.fudan.libdb.dao.util;

import java.util.Date;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class DateUtil {

    public static String currentTimestamp() {
        Date current = new Date();
        return ""+current.getTime();
    }

    public static Date getDateFromTimestamp(String timestamp) {
        Date date = new Date();
        date.setTime(Long.parseLong(timestamp));
        return date;
    }

    public static long delta(String timestamp1, String timestamp2) {
        long delta = getDateFromTimestamp(timestamp1).getTime() - getDateFromTimestamp(timestamp2).getTime();
        return Math.abs(delta);
    }
}
