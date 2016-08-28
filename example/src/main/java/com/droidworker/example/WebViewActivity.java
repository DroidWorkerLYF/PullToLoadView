package com.droidworker.example;

import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.impl.PullToLoadWebView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class WebViewActivity extends BaseActivity {
    private PullToLoadWebView mPullToLoadWebView;

    @Override
    public void onLoadNew() {
        mPullToLoadWebView.getContentView().reload();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPullToLoadWebView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.demo_4_title);

        mPullToLoadWebView = (PullToLoadWebView) findViewById(R.id.webview);
        if(mPullToLoadWebView != null){
            mPullToLoadWebView.setOnPullToLoadListener(this);
            WebView webView = mPullToLoadWebView.getContentView();

            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });

            webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);

                    if(newProgress == 100){
                        mPullToLoadWebView.onLoadComplete();
                    }
                }
            });

            webView.loadUrl("https://www.baidu.com");
        }
    }
}
