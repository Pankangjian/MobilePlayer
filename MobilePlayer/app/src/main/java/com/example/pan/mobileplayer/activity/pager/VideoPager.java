package com.example.pan.mobileplayer.activity.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.Adapter.VideoPagerAdapter;
import com.example.pan.mobileplayer.activity.activity.MyselfVideoPlayer;
import com.example.pan.mobileplayer.activity.base.BasePager;
import com.example.pan.mobileplayer.activity.domain.MediaItem;
import com.example.pan.mobileplayer.activity.utils.LogUtil;

import java.util.ArrayList;


/**
 * Created by pan on 2018/9/9.
 * 播放视频
 */
public class VideoPager extends BasePager {
    private TextView tv_no_video;
    private ListView lv_video;
    private ProgressBar pb_loading;
    private VideoPagerAdapter videoPagerAdapter;
    private ArrayList<MediaItem> mediaitems;  //装数据集合


    public VideoPager(Context context) {
        super(context);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaitems != null && mediaitems.size() > 0) {
                //有数据
                //设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context, mediaitems,true);
                lv_video.setAdapter(videoPagerAdapter);
                tv_no_video.setVisibility(View.GONE);
            } else {
                tv_no_video.setVisibility(View.VISIBLE);
            }
            pb_loading.setVisibility(View.GONE);
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        tv_no_video = (TextView) view.findViewById(R.id.tv_no_video);
        lv_video = (ListView) view.findViewById(R.id.lv_video);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //设置ListView的item的点击事件
        lv_video.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaitem = mediaitems.get(position);
            //调用系统所有的播放器 - 隐式意图
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaitem.getData()),"video/*");
//            context.startActivity(intent);
            //调用自己的播放器 - 显式式意图--一个播放地址
            /*Intent intent = new Intent(context,MyselfVideoPlayer.class);
//           Uri.parse(mediaitem.getData()为本地视频的绝对路径转化成uri
            intent.setDataAndType(Uri.parse(mediaitem.getData()),"video*//*");
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "你的手机上没有播放器哟!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            */
            /**
             *传递列表，序列化
             */
            Intent intent = new Intent(context, MyselfVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaitems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
    @Override
    public void initData() {
        super.initData();
        LogUtil.e("加载本地视频数据被加载了");
        //加载本地视频数据
        getDataFromLocal();
    }
    /**
     * 从本地的sdcard得到数据
     * //1.遍历sdcard,后缀名
     * //2.从内容提供者里面获取视频
     * //3.如果是6.0的系统，动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {
        mediaitems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
//                SystemClock.sleep(2000);   //加载视频时,转圈的等待时间2秒
                isGrantExternaRW((Activity) context);   //动态获取权限
                LogUtil.e("动态获取权限被获取了");
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sd卡的名字
                        MediaStore.Video.Media.DURATION,//视频的总时长
                        MediaStore.Video.Media.SIZE,    //视频的大小
                        MediaStore.Video.Media.DATA,    //视频的绝对地址
                        MediaStore.Video.Media.ARTIST   //视频的演出者
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaitem = new MediaItem();
                        mediaitems.add(mediaitem);

                        String name = cursor.getString(0);//视频文件在sd卡的名字
                        mediaitem.setName(name);

                        long duration = cursor.getLong(1);//视频的总时长
                        mediaitem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的大小
                        mediaitem.setSize(size);

                        String data = cursor.getString(3);//视频的绝对地址
                        mediaitem.setData(data);

                        String producer = cursor.getString(4);//视频的演出者
                        mediaitem.setProducer(producer);
                    }
                    cursor.close();
                }
                //发消息
                handler.sendEmptyMessage(10);

            }
        }.start();
    }
    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternaRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

}