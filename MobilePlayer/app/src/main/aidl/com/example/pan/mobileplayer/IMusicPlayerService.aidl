// IMusicPlayerService.aidl
package com.example.pan.mobileplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {

     /**
     *根据position位置打开对应的音乐
     *
     */
     void openAudio(int position);

    /**
     * 开始播放
     */
     void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
     void stop();

    /**
     * 得到当前的播放进度
     * @return
     */
    int getcurrentposition();

    /**
     * 得到当前播放音乐的总时长
     * @return
     */
     int getDuration();

    /**
     * 得到歌手名字
     * @return
     */
     String getArtist();

    /**
     * 得到歌曲名字
     * @return
     */
     String getName();

    /**
     * 得到歌曲播放路径
     * @return
     */
     String getAudioPath();

    /**
     * 播放下一个歌曲
     */
     void next();

    /**
     * 播放上一个歌曲
     */
     void pre();

    /**
     * 设置播放模式
     */
     void setPlayerMode(int playermode);

    /**
     * 得到播放模式
     */
     int getPlayerMode();

     /**
     * 是否正在播放
     */
     boolean isPlaying();

     /**
     * 拖动歌曲播放条
     */
     void seekTo(int position);

     /**
     * 音乐跳频显示
     */
     int getAudioSessionId();
}
