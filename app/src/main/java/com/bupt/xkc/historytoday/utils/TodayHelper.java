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


    public static String getTodayDate(String date){
        String[] todayDate = new String[3];

        if (date.startsWith("前")){
            date = date.substring(1);
        }

        if (date.startsWith("公元前")){
            date = date.substring(3);
        }

        todayDate[0] = date.substring(0,date.indexOf('年'));
        todayDate[1] = date.substring(date.indexOf('年') + 1, date.indexOf('月'));
        todayDate[2] = date.substring(date.indexOf('月') + 1, date.indexOf('日'));

        return todayDate[0]+"-"+todayDate[1]+"-"+todayDate[2];

    }

    public static int getYear(String date){
        int year = 0;

        if (date.contains("前")){
            year = Integer.parseInt(date.substring(date.indexOf("前") + 1, date.indexOf("年"))) * -1;
        }

        return 0;


    }


}
