package com.bupt.xkc.historytoday.utils;

import java.util.Calendar;

/**
 * Created by xkc on 4/8/16.
 */
public class TodayHelper {
    //得到今天的日期
    public static int[] getTodayDate() {
        int[] todayDate = new int[3];
        Calendar calendar = Calendar.getInstance();

        todayDate[0] = calendar.get(Calendar.YEAR);//年
        todayDate[1] = calendar.get(Calendar.MONTH) + 1;//月
        todayDate[2] = calendar.get(Calendar.DAY_OF_MONTH);//日

        return todayDate;

    }
}
