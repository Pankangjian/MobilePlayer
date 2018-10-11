package com.example.pan.mobileplayer.activity.pager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.base.BasePager;

/**
 * Created by pan on 2018/9/9.
 */
public class NetAudioPager extends BasePager{
    private WebView webview;
    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.webview, null);
        webview= (WebView) view.findViewById(R.id.webView);
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        WebSettings webSettings = webview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //加载需要显示的网页
     //   webview.loadUrl("http://music.baidu.com");   // 虾米音乐:http://www.xiami.com
      //  webview.loadUrl("https://music.laod.cn/");
        webview.loadUrl("http://www.xiami.com");
        //设置Web视图
        webview.setWebViewClient(new webViewClient());
      //  new getActivity();

        return view;
    }

    @Override
    public void initData() {
        super.initData();

    }

    //Web视图
    private class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url == null) return false;

            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                }
            } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return false;
            }
        }//在不是http:/https开头的网址上播放视频
    }

    private class getActivity extends Activity{
        @Override
        //设置回退
        //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
                webview.goBack(); //goBack()表示返回WebView的上一页面
                return true;
            }
            finish();//结束退出程序
            return false;
        }
    }

}
