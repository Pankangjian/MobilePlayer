package com.example.pan.mobileplayer.activity.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.domain.MediaItem;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by pan on 2018/9/12.
 */
public class NetVideoPagerAdapter extends BaseAdapter {

    private ArrayList<MediaItem> mediaitems;
    private Context context;

    public NetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaitems) {

        this.context = context;
        this.mediaitems = mediaitems;

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
            view = View.inflate(context, R.layout.item_netvideo_pager, null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHoder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);

            view.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) view.getTag();
        }

        //根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaitems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_desc.setText(mediaItem.getDesc());
        //1.使用xUtils3请求图片
        x.image().bind(viewHoder.iv_icon, mediaItem.getImageUrl());
         //2.使用Glide请求图片
//        Glide.with(context).load(mediaItem.getImageUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHoder.iv_icon);
//        //3.使用Picasso 请求图片
//        Picasso.with(context).load(mediaItem.getImageUrl())
////                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHoder.iv_icon);

        return view;
    }

    static class ViewHoder {

        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }
}