package com.example.pan.mobileplayer.activity.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pan.mobileplayer.activity.service.MusicPlayerService;

/**
 * 缓存工具类
 * Created by pan on 2018/9/27.
 */
public class CacheUtils {

    /**
     * 保存数据
     * @param context
     * @param Key
     * @param Values
     */
    public static void putString(Context context, String Key, String Values) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("pan", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(Key, Values).commit();
    }

    /**
     * 得到缓存的数据
     * @param context
     * @param Key
     * @return
     */
    public static String getString(Context context, String Key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("pan", Context.MODE_PRIVATE);
        return sharedpreferences.getString(Key, "");
    }

    /**
     * 保存播放模式
     * @param context
     * @param Key
     * @param values
     */
    public static void putPlaymode(Context context, String Key, int values){
        SharedPreferences sharedpreferences = context.getSharedPreferences("pan", Context.MODE_PRIVATE);
        sharedpreferences.edit().putInt(Key, values).commit();
    }

    /**
     * 得到播放模式
     * @param context
     * @param Key
     */
    public static int getPlaymode(Context context, String Key){
        SharedPreferences sharedpreferences = context.getSharedPreferences("pan", Context.MODE_PRIVATE);
        return sharedpreferences.getInt(Key, MusicPlayerService.REPEAT_NORMAL);
    }
}
