package com.example.pan.mobileplayer.activity.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.pan.mobileplayer.activity.domain.Lyric;
import com.example.pan.mobileplayer.activity.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by pan on 2018/10/6.
 * 自定义歌词显示
 */
public class ShowLyricView extends TextView {

    /**
     * 歌词列表数组
     */
    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint Wpaint;
    private int width;
    private int heigth;

    /**
     * 歌词列表中的索引
     */
    private int index;

    /**
     * 每一句歌词的高度
     */
    private float textHeight ;

    /**
     * 当前播放进度
     */
    private float currentposition;
    private float sleepTime;
    private float timePoint;


    /**
     * 设置歌词列表
     *
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        textHeight = DensityUtil.dip2px(context,25);//对应的像数
        //创建蓝色画笔
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(DensityUtil.dip2px(context,18));
        paint.setAntiAlias(true);
        //设置居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        //创建白色画笔
        Wpaint = new Paint();
        Wpaint.setColor(Color.WHITE);
        Wpaint.setTextSize(DensityUtil.dip2px(context,18));
        Wpaint.setAntiAlias(true);
        //设置居中对齐
        Wpaint.setTextAlign(Paint.Align.CENTER);


//        lyrics = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            Lyric lyric = new Lyric();
//            lyric.setTimePoint(1000 * i);
//            lyric.setSleepTime(1500 + i);
//            lyric.setContent(i + "潘潘潘潘潘潘潘潘" + i);
//            //把歌词添加到集合中
//            lyrics.add(lyric);
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        heigth = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {

            float plush = 0;
            if(sleepTime ==0){
                plush = 0;
            }else{
                //平移
                //这一句所花的时间 ：休眠时间 = 移动的距离 ： 总距离（行高）
                //移动的距离 =  (这一句所花的时间 ：休眠时间)* 总距离（行高）
//                float delta = ((currentPositon-timePoint)/sleepTime )*textHeight;

                //屏幕的的坐标 = 行高 + 移动的距离
                plush = textHeight + ((currentposition-timePoint)/sleepTime )*textHeight;
            }
            canvas.translate(0,-plush);

            //绘制歌词--绘制当前的歌词
            String currentText = lyrics.get(index).getContent();
            canvas.drawText(currentText, width / 2, heigth / 2, paint);

            //绘制前面的歌词
            float tempY = heigth / 2;//y轴的中间坐标
            for (int i = index - 1; i >= 0; i--) {
                //得到每一句歌词
                String preText = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                //绘制每上一句的歌词
                canvas.drawText(preText, width / 2, tempY, Wpaint);

            }
            tempY = heigth / 2;//y轴的中间坐标
            //绘制后面歌词
            for (int i = index + 1; i < lyrics.size(); i++) {
                //得到每一句歌词
                String nextText = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > heigth) {
                    break;
                }
                //绘制每下一句的歌词
                canvas.drawText(nextText, width / 2, tempY, Wpaint);

            }

        } else {
            //没有歌词
            canvas.drawText("没找到歌词", width / 2, heigth / 2, paint);
        }
    }

    public void setNextLyric(int currentposition) {
        this.currentposition = currentposition;
        if (lyrics == null || lyrics.size() == 0) {
            return;
        }
        for (int i = 1; i < lyrics.size(); i++) {

            if (currentposition < lyrics.get(i).getTimePoint()) {

                int tempIndex = i - 1;

                if (currentposition >= lyrics.get(tempIndex).getTimePoint()) {
                    //当前正在播放的哪句歌词
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }

            }
        }
        //重绘
        invalidate();//--在主线程中用方法
        //   postInvalidate();//--在子线程中用方法

    }
}

