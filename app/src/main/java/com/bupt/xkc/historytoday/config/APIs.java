package com.bupt.xkc.historytoday.config;

import android.os.Environment;

/**
 * 存放api 相关参数
 * Created by xkc on 4/7/16.
 */
public class APIs {
    public static final class REQUEST_URL{
        public static final String QUERY_EVENT = "http://v.juhe.cn/todayOnhistory/queryEvent.php";
        public static final String QUERY_DETAIL = "http://v.juhe.cn/todayOnhistory/queryDetail.php";
        public static final String API_KEY = "0b75bb31f32d98c840bca5ee0c51ea21";
    }


    public static final class RESPONSE_KEY{
        public static final String REASON = "reason";
        public static final String ERROR_CODE = "error_code";
    }

    public static final class LOCAL{
        public static final String SD_CARD = Environment.getExternalStorageDirectory().getPath();
        public static final String APP_FILE_DIR =  SD_CARD + "/HistoryToday";
        public static final String IMAGE_DIR = APP_FILE_DIR +"/pictures";
    }


}
