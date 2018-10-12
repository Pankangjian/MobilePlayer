package com.example.pan.mobileplayer.activity.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.domain.MediaItem;
import com.example.pan.mobileplayer.activity.utils.Utils;

import java.util.ArrayList;

/**
 * Created by pan on 2018/9/12.
 */
public class VideoPagerAdapter extends BaseAdapter {

    private final boolean isvideo;
    private  ArrayList<MediaItem> mediaitems;
    private  Context context;
    private Utils utils;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaitems, boolean isvideo){

        this.context = context;
        this.mediaitems = mediaitems;
        this.isvideo = isvideo;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return mediaitems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHoder viewHoder;
        if (view == null) {
            view = View.inflate(context, R.layout.item_video_pager, null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHoder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            viewHoder.tv_size = (TextView) view.findViewById(R.id.tv_size);

            view.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) view.getTag();
        }

        //根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaitems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        viewHoder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));

        if(!isvideo){
            viewHoder.iv_icon.setImageResource(R.drawable.music);
        }

        return view;
    }
    static class ViewHoder {

        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}