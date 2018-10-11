package com.example.pan.mobileplayer.activity.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.base.BasePager;
import com.example.pan.mobileplayer.activity.fragment.ReplaceFragment;
import com.example.pan.mobileplayer.activity.pager.AudioPager;
import com.example.pan.mobileplayer.activity.pager.NetAudioPager;
import com.example.pan.mobileplayer.activity.pager.NetVideoPager;
import com.example.pan.mobileplayer.activity.pager.VideoPager;

import java.util.ArrayList;


/**
 * Created by pan on 2018/9/9.
 */
public class MainActivity extends FragmentActivity {
    private RadioGroup rg_bottom_tag;
    private ArrayList<BasePager> basePagers; //页面集合
    private int position;
    private long mPressedTime = 0;//定义按退出程序键时间差

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));//添加本地视频页面 ----0
        basePagers.add(new AudioPager(this));//添加本地视频页面 ----1
        basePagers.add(new NetVideoPager(this));//添加本地视频页面 ----2
        basePagers.add(new NetAudioPager(this));//添加本地视频页面 ----3
        //设置RadioGrong的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video); //默认选中主页

    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId){
                default:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
            }
            setFragment();
        }
    }
    /**
     * 把页面添加到Fragment中
     */
    private void setFragment() {
        //1.得到FragmentManager
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft =manager.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main,new ReplaceFragment(getBasePager()));
        //4.提交事务
        ft.commit();
    }
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if(basePager != null&&!basePager.isInitData){
            basePager.initData();//联网请求或者绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {//比较两次按键时间差
            Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else {
//           Tuichu.getInstance().destroy();//退出程序
            /**
             * 用意图来返回桌面
             */
            Intent home=new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
    }

}
