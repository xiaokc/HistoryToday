package com.bupt.xkc.historytoday.utils;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity界面的启动和销毁操作
 * Created by xkc on 12/11/15.
 */
public class SysApplicationManager extends Application {

    private List<Activity> mList = new ArrayList<>();//用于存储每个启动的activity

    private static SysApplicationManager instance;

    private SysApplicationManager(){}

    //单例模式
    public synchronized  static SysApplicationManager getInstance(){
        if (null == instance){
            instance = new SysApplicationManager();
        }
        return instance;
    }

    //每个Activity启动时，将其添加到list中
    public void addActivity(Activity activity){
        mList.add(activity);
    }


    //关闭list中的每个activity，想要整个应用程序完全退出时调用此方法
    public void exit(){
        try {
            for (Activity activity:mList){
                if (activity != null){
                    activity.finish();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.exit(0);
        }

    }

    //杀进程
    @Override
    public void onLowMemory(){
        super.onLowMemory();
        System.gc();
    }

}
