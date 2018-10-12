package com.example.pan.mobileplayer.activity.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.domain.MediaItem;
import com.example.pan.mobileplayer.activity.utils.LogUtil;
import com.example.pan.mobileplayer.activity.utils.Utils;
import com.example.pan.mobileplayer.activity.view.VitamioVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by pan on 2018/9/20.
 */
public class VitamioVideoPlayer extends Activity implements View.OnClickListener {
    private VitamioVideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private LinearLayout llTopNtb;
    private TextView tvTopName;
    private ImageView ivTopBattery;
    private TextView tvTopTime;
    private LinearLayout llTopShenyin;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnPlayer;
    private LinearLayout llBottom;
    private TextView tvInitiate;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;
    private Utils utils;
    private Network network;
    private MyReceiver myReceiver;
    private TextView tv_buffer_speed;
    private LinearLayout ll_buffer;
    private TextView tv_loading_speed;
    private LinearLayout ll_loading;
    private float startY;
    private float touchRang;//屏幕距离
    private int mVol;       //按下的音量

    private static final int PROGRESS = 1; //视频进度的更新
    private static final int DEFAULT_SCREEN = 1;   //设置默认屏幕大小
    private static final int HIDE_MEDIA_CONTROLLER = 2;//隐藏播放控制器面板
    private static final int SHOW_SPEED = 3;           //显示网络速度
    private static final int FULL_SCREEN = 2;      //设置全屏
    private ArrayList<MediaItem> mediaItems;//传进来的视频列表
    private int position;                   //要播放的列表中视频的具体位置
    private GestureDetector detector;    //定义手势识别器
    private RelativeLayout media_controller;  //实例化media_controller
    private boolean isUseSystem = true;      //自定义切换卡顿处理  -直播视频与网络视频-
    private int previousPosition;
    private boolean isShowMediacontroller = false;
    private boolean isFullScreen = false;   //设置默认屏幕大小
    private int screenWidth = 0;           //得到屏幕的宽
    private int screenHeigth = 0;          //得到屏幕的高
    private int videoWidth;
    private int videoHeight;
    private AudioManager am;
    private int currentVoice;               //当前音量
    private int maxVoice;                   //最大音量 大小为0--15
    private boolean isMute = false;         //设置是否静音
    private boolean isNetUri;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-13 23:19:29 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_vitamio_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        llTopNtb = (LinearLayout) findViewById(R.id.ll_top_ntb);
        tvTopName = (TextView) findViewById(R.id.tv_top_name);
        ivTopBattery = (ImageView) findViewById(R.id.iv_top_battery);
        tvTopTime = (TextView) findViewById(R.id.tv_top_time);
        llTopShenyin = (LinearLayout) findViewById(R.id.ll_top_shenyin);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnPlayer = (Button) findViewById(R.id.btn_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvInitiate = (TextView) findViewById(R.id.tv_initiate);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSiwchScreen = (Button) findViewById(R.id.btn_video_siwch_screen);
        videoview = (VitamioVideoView) findViewById(R.id.videoview);
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_buffer_speed = (TextView) findViewById(R.id.tv_buffer_speed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_loading_speed = (TextView) findViewById(R.id.tv_loading_speed);

        btnVoice.setOnClickListener(this);
        btnPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSiwchScreen.setOnClickListener(this);

        //最大值与seekbar关联
        seekbarVoice.setMax(maxVoice);
        //设置当前的音量进度
        seekbarVoice.setProgress(currentVoice);

        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-09-13 23:19:29 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
            isMute = !isMute;      //如果点击总是取反值
            updataVoice(currentVoice, isMute);
        } else if (v == btnPlayer) {
            // Handle clicks for btnPlayer
            showPlayer();
        } else if (v == btnExit) {
            // Handle clicks for btnExit  退出
            finish();
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            PrePlayer();
        } else if (v == btnVideoStartPause) {
            setStartPause();
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            NextPlayer();
        } else if (v == btnVideoSiwchScreen) {
            // Handle clicks for btnVideoSiwchScreen
            setFullScreen();
        }
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
    }
    private void showPlayer() {
        AlertDialog.Builder bundle = new AlertDialog.Builder(this);
        bundle.setTitle("提示");
        bundle.setMessage("是否切换回系统播放器");
        bundle.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startSystemPlayer();
            }
        });
        bundle.setNegativeButton("取消",null);    //取消后为空-继续系统播放
        bundle.show();
    }

    private void startSystemPlayer() {
        if(videoview != null){
            videoview.stopPlayback();
        }
        Intent intent = new Intent(this,MyselfVideoPlayer.class);
        if (mediaItems != null && mediaItems.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            startActivity(intent);
        } else if (uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
    }

    private void setStartPause() {
        //设置开始--暂停
        if (videoview.isPlaying()) {
            videoview.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            videoview.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个按钮
     */
    private void PrePlayer() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放上一个
            position--;
            ll_loading.setVisibility(View.VISIBLE);
            if (position >= 0) {
                MediaItem mediaItem = mediaItems.get(position);
                tvTopName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());
                //设置按钮状态,变为不可按
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态,变为不可按
            setButtonState();
        }
    }

    /**
     * 播放下一个按钮
     */
    private void NextPlayer() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放下一个
            position++;
            ll_loading.setVisibility(View.VISIBLE);
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvTopName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());
                //设置按钮状态,变为不可按
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态,变为不可按
            setButtonState();
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                ButtonSwitch(false);
            } else if (mediaItems.size() == 2) {
                if (position == 0) {
                    //刚播放第一个视频时上一个按钮不可点击,下一个可点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == mediaItems.size() - 1) {
                    //播放到最后一个视频时下一个按钮不可点击，上一个按钮可点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            } else {
                if (position == 0) {
                    //刚播放第一个视频时上一个按钮不可点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (position == mediaItems.size() - 1) {
                    // /播放到最后一个视频时下一个按钮不可点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {
                    ButtonSwitch(true);//其余情况下都可点击
                }
            }

        } else if (uri != null) {
            //按钮设置灰色-不可按
            ButtonSwitch(false);
        }
    }

    private void ButtonSwitch(boolean isSwitch) {
        if (isSwitch == true) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        } else if ((isSwitch == false)) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_SPEED://显示网络速度
                    //得到网络速度
                    String netspeed = utils.getNetSpeed(VitamioVideoPlayer.this);
                    tv_loading_speed.setText("加载中..." + netspeed);
                    tv_buffer_speed.setText("缓冲中..." + netspeed);
                    //每2秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);//延迟2秒
                    break;
                case HIDE_MEDIA_CONTROLLER://隐藏控制面板
                    hideMediacontroller();
                    break;
                case PROGRESS:
                    //得到当前的视频播放进度
                    int currentPosition = (int) videoview.getCurrentPosition();
                    //当前的进度
                    seekbarVideo.setProgress(currentPosition);

                    //更新文本进度
                    tvInitiate.setText(utils.stringForTime(currentPosition));
                    //设置系统时间
                    tvTopTime.setText(getSystemTime());

                    //缓冲进度的更新
                    if (isNetUri) {
                        //只有网络资源才有缓冲效果
                        int buffer = videoview.getBufferPercentage();
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    //自定义监听卡
                    if (!isUseSystem && videoview.isPlaying()) {

                        if (videoview.isPlaying()) {

                            int buffer = currentPosition - previousPosition;
                            if (buffer < 500) {
                                ll_buffer.setVisibility(View.VISIBLE); //卡顿时显示
                            } else {
                                ll_buffer.setVisibility(View.GONE);    //不卡顿时隐藏
                            }
                        } else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }
                    //每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);//延迟1秒
                    break;

            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat DateFormat = new SimpleDateFormat("HH:mm");//系统时间格式
        return DateFormat.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //初始化父类
        inintData();
        findViews();
        setListener();
        //得到播放地址
        getData();
        setData();
//        videoview.setMediaController(new MediaController(this));    //调用系统的控制面板
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvTopName.setText(mediaItem.getName());   //设置视频的名称
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            tvTopName.setText(uri.toString());
            isNetUri = utils.isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        } else {
            Toast.makeText(VitamioVideoPlayer.this, "没有数据传递！！！!", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void inintData() {
        utils = new Utils();
        //注册电量广播
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量发生变化时发送广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myReceiver, intentFilter);

        //实例化手势识别器，重写方法手势- 长按 - 单击 - 双击 -
        detector =
                new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                    //双击手势
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        setFullScreen();
                        return super.onDoubleTap(e);
                    }

                    //长按手势
                    @Override
                    public void onLongPress(MotionEvent e) {
                        setStartPause();
                        super.onLongPress(e);
                    }

                    //单击手势
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (isShowMediacontroller) {
                            hideMediacontroller();
                            //发消息移除隐藏
                            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                        } else {
                            ShowMediacontroller();
                            //发消息隐藏
                            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                });
        //得到屏幕的宽和高
//        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        screenHeigth = getWindowManager().getDefaultDisplay().getHeight();
        /**
         * 最新方法得到宽和高
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeigth = displayMetrics.heightPixels;

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void setFullScreen() {
        if (isFullScreen) {
            //默认大小
            setVideoTpye(DEFAULT_SCREEN);
        } else {
            //全屏
            setVideoTpye(FULL_SCREEN);
        }
    }

    private void setVideoTpye(int defaultScreen) {
        switch (defaultScreen) {
            case FULL_SCREEN://全屏
                isFullScreen = true;
                videoview.setVideoSize(screenWidth, screenHeigth);
                //设置按钮的状态--默认
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);

                break;
            case DEFAULT_SCREEN://默认
                isFullScreen = false;
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeigth;
                // for compatibility, we adjust size based on aspect ratio--
                // 默认的兼容视频的宽和高的算法
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width, height);

                //设置按钮的状态
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                break;
        }

    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0); //电量等级0-100
            setBattery(level);

        }
    }

    public void setBattery(int level) {
        if (level <= 0) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivTopBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());
        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        //设置SeeKbar视频状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        //设置SeeKbar声音状态变化的监听
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        if (isUseSystem) {
            //视频播放时卡顿的监听 - 用系统的api-
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoview.setOnInfoListener(new MyOnInfoListener());
            } else {

            }
        }

    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int j) {
            switch (i) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    ll_buffer.setVisibility(View.VISIBLE);  //卡顿时显示
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ll_buffer.setVisibility(View.GONE);    //隐藏
                    break;
            }
            return true;
        }
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
            if (user) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                updataVoice(progress, false);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    private void updataVoice(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                videoview.seekTo(i);
            }
        }

        /**
         * 手指触碰后回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 手指离开后回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            videoWidth = mediaPlayer.getVideoWidth();
            videoHeight = mediaPlayer.getVideoHeight();
            /**
             * 开始播放
             */
            videoview.start();
//            mediaPlayer.getDuration();
            //视频的总时长,关联总长度
            int duration = (int) videoview.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));
            hideMediacontroller();//默认隐藏控制面板

            //发消息
            handler.sendEmptyMessage(PROGRESS);
            //设置宽和高参数
            // videoview.setVideoSize(200,200);
            /**
             * 视频真实的高和宽
             */
//            videoview.setVideoSize(mediaPlayer.getVideoWidth(),mediaPlayer.getVideoHeight());

            //屏幕默认的播放
            setVideoTpye(DEFAULT_SCREEN);

            //把加载页面消掉
            ll_loading.setVisibility(View.GONE);
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
//            Toast.makeText(VitamioVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
            showError();
            return true;
        }
    }

    private void showError() {
        AlertDialog.Builder bundle = new AlertDialog.Builder(this);
        bundle.setTitle("错误提示");
        bundle.setMessage("无法播放该视频！！");
        bundle.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        bundle.show();
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //    Toast.makeText(MyselfVideoPlayer.this, "播放结束了" + uri, Toast.LENGTH_SHORT).show();
            NextPlayer();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart--");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop--");
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        super.onDestroy();
        LogUtil.e("onDestroy--");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);  //把事件传递到手势识别器
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                //按下记录值
                startY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeigth, screenWidth);
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //移动的记录相关值
                float endY = event.getY();
                float distanceY = startY - endY;
                //改变声音 = （滑动屏幕的距离：总距离）*音量最大值
                float delta = (distanceY / touchRang) * maxVoice;
                //最终的声音 = 原来的声音 + 改变的声音
                int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                if (delta != 0) {
                    isMute = false;
                    updataVoice(voice, isMute);
                }
                break;
            case MotionEvent.ACTION_UP://手指离开
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 显示控制面板
     */
    private void ShowMediacontroller() {
        media_controller.setVisibility(View.VISIBLE);
        isShowMediacontroller = true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediacontroller() {
        media_controller.setVisibility(View.GONE);
        isShowMediacontroller = false;
    }

    /**
     * 物理按键声音处理
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            updataVoice(currentVoice, false);
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updataVoice(currentVoice, false);
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
