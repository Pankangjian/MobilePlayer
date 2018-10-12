package com.example.pan.mobileplayer.activity.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.activity.SearchActivity;

import junit.framework.Test;

/**
 * Created by pan on 2018/9/10.
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {
    private View tv_title__sousuo;

    private View rl_game;

    private View iv_record;

    private Context context;

    /**
     * 在代码中实例化该类的时候使用这个方法
     */
    public TitleBar(Context context) {
        this(context,null);
    }
    /**
     * 当在布局文件使用该类的时候，Android系统通过这个构造方法实例化该类
     */
    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    /**
     * 当需要设置样式的时候，可以使用该方法
     */
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context =context;
    }
    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子的实例
        tv_title__sousuo = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);

        //设置点击事件
        tv_title__sousuo.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_title__sousuo:
                Intent intent = new Intent(context,SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(context,"游戏",Toast.LENGTH_SHORT).show();

                break;
            case R.id.iv_record:
                Toast.makeText(context,"播放记录",Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
