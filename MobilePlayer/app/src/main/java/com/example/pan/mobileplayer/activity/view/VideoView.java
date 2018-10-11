package com.example.pan.mobileplayer.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by pan on 2018/9/18.
 * 自定义videoview
 */
public class VideoView extends android.widget.VideoView{
    public VideoView(Context context) {
        super(context,null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 设置视频宽和高
     * @param videowidth
     * @param videoheight
     */
    public void setVideoSize (int videowidth, int videoheight){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = videowidth;
        params.height = videoheight;
        setLayoutParams(params);
    }
}
