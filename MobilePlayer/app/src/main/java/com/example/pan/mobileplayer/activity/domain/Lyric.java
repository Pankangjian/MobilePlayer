package com.example.pan.mobileplayer.activity.domain;

/**
 * Created by pan on 2018/10/6.
 * 歌词类
 */
public class Lyric {

    /**
     * 歌词内容
     */
    private String content;

    /**
     * 时间点
     */
    private long timePoint;

    /**
     * 休眠显示的时间
     */
    private long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }

}
