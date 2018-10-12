package com.example.pan.mobileplayer.activity.utils;

import android.app.Activity;
import android.app.Application;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2018/10/7.
 * 退出程序工具类
 */
public class Tuichu extends Application {

    public static List<Object> activitys = new ArrayList<Object>();
    private static Tuichu instance;
    //获取单例模式中唯一的MyApplication实例
    public static Tuichu getInstance() {
        if (instance == null)
            instance = new Tuichu();
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (!activitys.contains(activity))
            activitys.add(activity);
    }

    // 遍历所有Activity并finish
    public void destroy() {
        for (Object activity : activitys) {
            ((Activity) activity).finish();
        }
        System.exit(0);
    }
}