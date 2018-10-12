package com.example.pan.mobileplayer.activity.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pan.mobileplayer.IMusicPlayerService;
import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.service.MusicPlayerService;
import com.example.pan.mobileplayer.activity.utils.LyricUtils;
import com.example.pan.mobileplayer.activity.utils.Utils;
import com.example.pan.mobileplayer.activity.view.BaseVisualizerView;
import com.example.pan.mobileplayer.activity.view.ShowLyricView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2018/10/3.
 */
public class AudioPlayer extends Activity implements View.OnClickListener {
    private int position;
    private IMusicPlayerService service;
    private ImageView ivIcon;
    private BaseVisualizerView baseVisualizerView;
    private TextView tvArtist;
    private TextView tvName;
    private LinearLayout llKz;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private MyReceiver receiver;
    private Utils utils;
    private TextView tvProgressTime;
    private TextView tvCumulativeTime;
    private ShowLyricView showLyricView;
    private boolean notification;
    private static final int PROGESS = 1;//进度更新条
    private static final int SHOW_LYRIC = 2;//显示歌词

    /**
     * 启动服务con
     */
    private ServiceConnection con = new ServiceConnection() {
        /**
         * 当连接成功的时候回调此方法
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    if (!notification) {
                        //从列表进入
                        service.openAudio(position);
                    } else {
                        //从状态栏进入
                        showViewData();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开连接时回调此方法
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            try {
                if (service != null) {
                    service.stop();
                    service = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-10-05 18:38:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {

        setContentView(R.layout.activity_audio_player);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        baseVisualizerView = (BaseVisualizerView) findViewById(R.id.baseVisualizerView);
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivIcon.getBackground();
        animationDrawable.start();
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        llKz = (LinearLayout) findViewById(R.id.ll_kz);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyrc = (Button) findViewById(R.id.btn_lyrc);
        tvProgressTime = (TextView) findViewById(R.id.tv_ProgressTime);
        tvCumulativeTime = (TextView) findViewById(R.id.tv_CumulativeTime);
        showLyricView = (ShowLyricView) findViewById(R.id.showLyricView);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);

        /**
         * 设置歌曲进度条拖动
         */
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
            if (fromUser) {
                //拖动进度条
                try {
                    service.seekTo(i);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:
                    try {
                        //1.得到当前的进度
                        int currentposition = service.getcurrentposition();

                        //2.把进度传入ShowLyricView控件，并且计算该高亮哪一句
                        showLyricView.setNextLyric(currentposition);

                        //3.实时的发消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                case PROGESS:
                    try {
                        //1---得到当前的进度
                        int currentposition = service.getcurrentposition();

                        //2---设置进度
                        seekbarAudio.setProgress(currentposition);

                        //3---进度更新
                        tvProgressTime.setText(utils.stringForTime(currentposition));
                        tvCumulativeTime.setText(utils.stringForTime(service.getDuration()));

                        //每秒更新一次
                        handler.removeMessages(PROGESS);  //移除
                        handler.sendEmptyMessageDelayed(PROGESS, 1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();//注册广播
        findViews();//初始化控件
        getData();  //获取数据
        bindstartService();//绑定服务
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-10-05 18:38:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            // 播放模式
            setPlaymode();
        } else if (v == btnAudioPre) {
            // 上一曲
            if (service != null) {
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioStartPause) {
            /**
             * 开始与暂停播放
             */
            if (service != null) {
                try {
                    if (service.isPlaying()) {
                        //暂停
                        service.pause();
                        //设置图标为播放图标
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //播放
                        service.start();
                        //设置图标为暂停图标
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        } else if (v == btnAudioNext) {
            // 下一曲
            if (service != null) {
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnLyrc) {
            // Handle clicks for btnLyrc
        }
    }

    private void setPlaymode() {

        try {
            int playmode = service.getPlayerMode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            //保存
            service.setPlayerMode(playmode);

            //设置图片
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {
        try {
            int playmode = service.getPlayerMode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayer.this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayer.this, "单曲播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayer.this, "全部播放", Toast.LENGTH_SHORT).show();
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayer.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验播放模式状态
     */
    private void checkPlaymode() {
        try {
            int playmode = service.getPlayerMode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        /**
         * 注册广播
         */
        utils = new Utils();
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);//registerReceiver注册广播方法，intentFilter为意图

    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //发消息显示歌词
            showLyric();
            showViewData();//显示歌曲信息方法
            checkPlaymode();//校验播放模式状态
            setupVisualizerFxAndUi();

        }
    }

    private Visualizer mVisualizer;
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {
        try {
            askPermission();
            int audioSessionid = service.getAudioSessionId();
            System.out.println("audioSessionid=="+audioSessionid);
            mVisualizer = new Visualizer(audioSessionid);
            // 参数内必须是2的位数
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // 设置允许波形表示，并且捕获它
            baseVisualizerView.setVisualizer(mVisualizer);
            mVisualizer.setEnabled(true);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * android6.0以上授权调用系统类Visualizer
     */
    List<String> permissions = new ArrayList<String>();
    private boolean askPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int RECORD_AUDIO = checkSelfPermission( Manifest.permission.RECORD_AUDIO );
            if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            } else
                return false;
        } else
            return false;
        return true;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            boolean result = true;
            for (int i = 0; i < permissions.length; i++) {
                result = result && grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
            if (!result) {
                Toast.makeText(this, "授权结果（至少有一项没有授权），result="+result, Toast.LENGTH_LONG).show();
                // askPermission();
            } else {
                //授权成功
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    private void showLyric() {
    //解析歌词
        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = service.getAudioPath();//得到歌曲的绝对路径

            //传歌词文件
            //mnt/sdcard/audio/beijingbeijing.krc  酷狗歌词文件
            //mnt/sdcard/audio/beijingbeijing.mp3
            //mnt/sdcard/audio/beijingbeijing.lrc
            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path + ".lrc");
            if(!file.exists()){
                file = new File(path + ".txt");

            }
            lyricUtils.readLyric(file);//解析歌词

            showLyricView.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);
        }

    }

    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            //设置进度条最大值
            seekbarAudio.setMax(service.getDuration());
            //发消息
            handler.sendEmptyMessage(PROGESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindstartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.example.pan.mobileplayer_OPENAUDIO");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 得到数据
     */
    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }


    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);//移除handler

        //取消广播
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;  //设置为null时垃圾回收机制会优先回收
        }

        //解绑服务
        if (con != null) {
            unbindService(con);
            con = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVisualizer != null)
        {
            mVisualizer.release();
        }
    }
}
