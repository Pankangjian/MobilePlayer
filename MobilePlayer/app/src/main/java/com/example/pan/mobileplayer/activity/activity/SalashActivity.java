package com.example.pan.mobileplayer.activity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.pan.mobileplayer.R;
import java.util.Timer;
import java.util.TimerTask;

public class SalashActivity extends Activity {

    private ImageView imageview;
    private final int SKIP_DELAY_TIME = 10000;
    private boolean isStartMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salash);
        imageview = (ImageView) findViewById(R.id.iv_salash);
        Glide.with(this).load(R.drawable.loading).asGif().into(imageview);  //加载本地动静态图
//        Glide.with(this)
//                .load("http://nuuneoi.com/uploads/source/playstore/cover.jpg")//加载网络图片
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imageview);
        Timer time = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startMainActivity();
            }
        };
        time.schedule(task, SKIP_DELAY_TIME);
    }

    private void startMainActivity() {
        if (!isStartMain) {
            isStartMain = true;
            startActivity(new Intent(SalashActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 屏蔽返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog(2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

/*public class SalashActivity extends Activity {

    private static final String TAG =SalashActivity.class.getSimpleName();//"SplashActivity"

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salash);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后才执行到这里
                //执行在主线程中
                startMainActivity();
                Log.e(TAG, "当前线程名称==" + Thread.currentThread().getName());
            }
        }, 2000);
    }

    private boolean isStartMain = false;
    *//**
     * 跳转到主页面，并且把当前页面关闭掉
     *//*
    private void startMainActivity() {
        if(!isStartMain){
            isStartMain = true;
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            //关闭当前页面
            finish();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent==Action"+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        //把所有的消息和回调移除
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}*/
