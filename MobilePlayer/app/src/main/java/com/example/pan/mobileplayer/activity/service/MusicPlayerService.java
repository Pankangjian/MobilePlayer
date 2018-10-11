package com.example.pan.mobileplayer.activity.service;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.example.pan.mobileplayer.IMusicPlayerService;
import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.activity.AudioPlayer;
import com.example.pan.mobileplayer.activity.domain.MediaItem;
import com.example.pan.mobileplayer.activity.utils.CacheUtils;
import com.example.pan.mobileplayer.activity.utils.LogUtil;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by pan on 2018/10/4.
 */
public class MusicPlayerService extends Service {
    public static final String OPENAUDIO = "com.example.pan.mobileplayer_OPENAUDIO";
    private ArrayList<MediaItem> mediaitems;
    private int position;
    private MediaItem mediaItem; //当前播放的歌曲文件对象
    private MediaPlayer mediaPlayer;//用于播放歌曲
    private NotificationManager manager;

    /**
     * 默认--顺序循环播放
     */
    public static final int REPEAT_NORMAL = 1;

    /**
     * 切换--单曲循环播放
     */
    public static final int REPEAT_SINGLE = 2;

    /**
     * 切换--全部播放
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 切换--随机循环播放
     */
    public static final int REPEAT_RANDOM = 4;

    /**
     * 播放模式
     */
    private int playermode = REPEAT_NORMAL;

    @Override

    public void onCreate() {
        super.onCreate();
        //得到保存的播放模式
        playermode = CacheUtils.getPlaymode(this, "playermode");
        //加载音乐列表
        getDataFromlist();
    }

    /**
     * 得到数据列表
     */
    private void getDataFromlist() {
        mediaitems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //           isGrantExternaRW((Activity) context);    //动态获取权限
                LogUtil.e("动态获取权限被获取了");
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//歌曲文件在sd卡的名字
                        MediaStore.Audio.Media.DURATION,//歌曲的总时长
                        MediaStore.Audio.Media.SIZE,    //歌曲的大小
                        MediaStore.Audio.Media.DATA,    //歌曲的绝对地址
                        MediaStore.Audio.Media.ARTIST   //歌曲的演出者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaitem = new MediaItem();
                        mediaitems.add(mediaitem);

                        String name = cursor.getString(0);//歌曲文件在sd卡的名字
                        mediaitem.setName(name);

                        long duration = cursor.getLong(1);//歌曲的总时长
                        mediaitem.setDuration(duration);

                        long size = cursor.getLong(2);//歌曲的大小
                        mediaitem.setSize(size);

                        String data = cursor.getString(3);//歌曲的绝对地址
                        mediaitem.setData(data);

                        String producer = cursor.getString(4);//歌曲的演出者
                        mediaitem.setProducer(producer);
                    }
                    cursor.close();
                }

            }
        }.start();
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {

        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {

            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getcurrentposition() throws RemoteException {
            return service.getcurrentposition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayerMode(int playermode) throws RemoteException {
            service.setPlayerMode(playermode);
        }

        @Override
        public int getPlayerMode() throws RemoteException {
            return service.getPlayerMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mediaPlayer.seekTo(position);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    /**
     * 根据position位置打开对应的音乐,并播放
     */
    private void openAudio(int position) {
        //获取列表位置
        this.position = position;
        if (mediaitems != null && mediaitems.size() > 0) {
            mediaItem = mediaitems.get(position);

            if (mediaPlayer != null) {
                //    mediaPlayer.release(); //结束
                mediaPlayer.reset();   //释放
            }

            try {
                mediaPlayer = new MediaPlayer();
                //设置监听--播放出错--播放完成，准备好
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());//准备
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());//播放完成
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());//播放出错
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();//回调
                /**
                 * 配置单曲循环播放,setLooping不回调播放完成
                 */
                if (playermode == MusicPlayerService.REPEAT_SINGLE) {
                    mediaPlayer.setLooping(true);
                } else {
                    mediaPlayer.setLooping(false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(MusicPlayerService.this, "没有数据", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放出错监听
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            next();
            return true;
        }
    }

    /**
     * 播放完成监听
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next();
        }
    }

    /**
     * 播放准备监听
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //通知activity来获取歌曲信息--通过广播
            notifyChange(OPENAUDIO);
            start();
        }
    }

    /**
     * 根据动作发送广播
     *
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        //发送广播方法
        sendBroadcast(intent);
    }


    /**
     * 开始播放
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void start() {
        mediaPlayer.start();

        /**
         * 通知栏显示
         */
        //当播放歌曲时，在状态栏显示播放，点击时进入音乐播放界面
        Intent intent = new Intent(this, AudioPlayer.class);
        intent.putExtra("notification", true);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("我的音乐")
                .setContentText("正在播放--" + getName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);
    }

    /**
     * 暂停播放
     */
    private void pause() {
        mediaPlayer.pause();
        manager.cancel(1);
    }

    /**
     * 停止播放
     */
    private void stop() {

    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getcurrentposition() {

        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到当前播放音乐的总时长
     *
     * @return
     */
    private int getDuration() {

        return mediaPlayer.getDuration();
    }

    /**
     * 得到歌手名字
     *
     * @return
     */
    private String getArtist() {

        return mediaItem.getProducer();
    }

    /**
     * 得到歌曲名字
     *
     * @return
     */
    private String getName() {

        return mediaItem.getName();
    }

    /**
     * 得到歌曲播放路径
     *
     * @return
     */
    private String getAudioPath() {

        return mediaItem.getData();
    }

    /**
     * 播放下一个歌曲
     */
    private void next() {

        //根据当前的播放模式--设置下一个位置
        setNextPosition();

        //根据当前的播放模式和下标位置去播放歌曲
        openNextAudio();

    }

    private void openNextAudio() {
        int playmode = getPlayerMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if (position < mediaitems.size()) {
                //正常范围
                openAudio(position);
            } else {
                position = mediaitems.size() - 1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position < mediaitems.size()) {
                //正常范围
                openAudio(position);
            } else {
                position = mediaitems.size() - 1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayerMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position++;   //顺序播放模式--播放下一首
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            position++;
            if (position >= mediaitems.size()) {
                position = 0;//单曲播放模式--重复播放单曲
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position++;
            if (position >= mediaitems.size()) {
                position = 0;////全部播放模式--播放完最后一首后切换回第一首
            }
        } else {
            position++;
        }
    }

    /**
     * 播放上一个歌曲
     */
    private void pre() {

        //根据当前的播放模式--设置上一个位置
        setPrePosition();

        //根据当前的播放模式和下标位置去播放歌曲
        openPreAudio();
    }

    private void openPreAudio() {
        int playmode = getPlayerMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if (position >= 0) {
                //正常范围
                openAudio(position);
            } else {
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position >= 0) {
                //正常范围
                openAudio(position);
            } else {
                position = 0;
            }
        }
    }

    private void setPrePosition() {
        int playmode = getPlayerMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position--;   //顺序播放模式--播放上一首
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            position--;
            if (position < 0) {
                position = mediaitems.size() - 1;//单曲播放模式--重复播放单曲
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position--;
            if (position < 0) {
                position = mediaitems.size() - 1;////全部播放模式--播放完最后一首后切换回第一首
            }
        } else {
            position--;
        }
    }

    /**
     * 设置播放模式
     */
    private void setPlayerMode(int playermode) {
        this.playermode = playermode;
        CacheUtils.putPlaymode(this, "playermode", playermode);

        if (playermode == MusicPlayerService.REPEAT_SINGLE) {
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setLooping(false);
        }
    }

    /**
     * 得到播放模式
     */
    private int getPlayerMode() {

        return playermode;
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}
