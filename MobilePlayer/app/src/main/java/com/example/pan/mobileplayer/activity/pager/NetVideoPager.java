package com.example.pan.mobileplayer.activity.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.Adapter.NetVideoPagerAdapter;
import com.example.pan.mobileplayer.activity.activity.MyselfVideoPlayer;
import com.example.pan.mobileplayer.activity.base.BasePager;
import com.example.pan.mobileplayer.activity.domain.MediaItem;
import com.example.pan.mobileplayer.activity.utils.CacheUtils;
import com.example.pan.mobileplayer.activity.utils.Constants;
import com.example.pan.mobileplayer.activity.utils.LogUtil;
import com.example.pan.mobileplayer.activity.view.XListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pan on 2018/9/9.
 * 网络视频
 */
public class NetVideoPager extends BasePager {
    private boolean isLoadMore = false;
    private NetVideoPagerAdapter netVideoPagerAdapter;

    @ViewInject(R.id.iv_net_no)
    private ImageView imageView;

    @ViewInject(R.id.lv_netvideo)
    private XListView mlistView;

    @ViewInject(R.id.tv_net_video)
    private TextView mtextView;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mprogressBar;
    private ArrayList<MediaItem> mediaItems;

    public NetVideoPager(Context context) {
        super(context);
    }

    /**
     * 初始化当前页面的控件
     *
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_pager, null);
        //第一个参数为NetVideopager.this,第二个参数为布局
        x.view().inject(NetVideoPager.this, view);
        mlistView.setOnItemClickListener(new MyOnItemClickListener());
        //下拉刷新设置
        mlistView.setPullLoadEnable(true);
        mlistView.setXListViewListener(new setXListViewListener());
        return view;
    }

    class setXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            getDataNetworKing();
        }

        @Override
        public void onLoadMore() {
            getMoreNetData();
        }
    }

    private void getMoreNetData() {
        //联网请求
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
                isLoadMore = true;
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("联网取消==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("结束");
                isLoadMore = false;
            }
        });
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /**
             *传递列表，序列化
             */
            Intent intent = new Intent(context, MyselfVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position - 1);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频数据被初始化");
        String saveJson = CacheUtils.getString(context, Constants.NET_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        } else {
            getDataNetworKing();
        }
    }

    private void getDataNetworKing() {
        //联网请求
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
                //缓存数据
                CacheUtils.putString(context, Constants.NET_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
                  showNetData();
//                imageView.setVisibility(View.VISIBLE);
//                mtextView.setVisibility(View.VISIBLE);
//                mprogressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("联网取消==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("结束");
            }
        });
    }

    private void processData(String json) {
        //设置是否加载更多，默认为false
        if (!isLoadMore) {
            mediaItems = parseJson(json);
            showNetData();

        } else {
            isLoadMore = false;
            //加载更多，把更多的数据添加到原来的集合中
            ArrayList<MediaItem> moreDatas = parseJson(json);
            mediaItems.addAll(moreDatas);      // 代码可截减为mediaItems.addAll(parseJson(json));
            //刷新适配器
            netVideoPagerAdapter.notifyDataSetChanged();
            onLoad();
        }
    }

    private void showNetData() {
        //设置适配器
        if (mediaItems != null && mediaItems.size() > 0) {

            netVideoPagerAdapter = new NetVideoPagerAdapter(context, mediaItems);
            mlistView.setAdapter(netVideoPagerAdapter);
            onLoad();
            mtextView.setVisibility(View.GONE);
        } else {
//          mprogressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            mtextView.setVisibility(View.VISIBLE);
        }
        mprogressBar.setVisibility(View.GONE);
    }

    private String getSystemTime() {
        SimpleDateFormat DateFormat = new SimpleDateFormat("HH:mm");//系统时间格式
        return DateFormat.format(new Date());
    }

    private void onLoad() {
        mlistView.stopRefresh();
        mlistView.stopLoadMore();
        mlistView.setRefreshTime("更新时间:" + getSystemTime());                  //得到系统刷新时间
    }

    /**
     * 解析json数据
     * 1.用系统接口解析json数据
     * 2.使用第三方解析工具（gson,fastjson）
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<MediaItem>();

        try {
            JSONObject jsonobject = new JSONObject(json);
            JSONArray jsonArray = jsonobject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobjectItem = (JSONObject) jsonArray.get(i);
                    if (jsonobjectItem != null) {
                        MediaItem mediaItem = new MediaItem();

                        String movieName = jsonobjectItem.optString("movieName"); //name
                        mediaItem.setName(movieName);

                        String videoTitle = jsonobjectItem.optString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);

                        String imageUrl = jsonobjectItem.optString("coverImg");//imageurl
                        mediaItem.setImageUrl(imageUrl);

                        String hightUrl = jsonobjectItem.optString("hightUrl");//data
                        mediaItem.setData(hightUrl);

                        //把数据添加到集合
                        mediaItems.add(mediaItem);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }
}
