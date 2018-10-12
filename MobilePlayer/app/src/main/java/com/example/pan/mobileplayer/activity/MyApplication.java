package com.example.pan.mobileplayer.activity;

import android.app.Application;
import com.example.pan.mobileplayer.BuildConfig;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import org.xutils.x;


/**
 * Created by pan on 2018/9/22.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        // 将“5bb9b310”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在“ =” 与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5bb9b310");
    }
}
