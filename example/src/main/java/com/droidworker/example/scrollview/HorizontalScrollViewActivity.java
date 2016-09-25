package com.droidworker.example.scrollview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.droidworker.example.BaseActivity;
import com.droidworker.example.R;
import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.impl.scrollview.PTLHorizontalScrollView;

/**
 * @author luoyanfeng@le.com
 */
public class HorizontalScrollViewActivity extends BaseActivity {
    private PTLHorizontalScrollView mPTLHorizontalScrollView;

    @Override
    public void onLoadNew() {
        mPTLHorizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPTLHorizontalScrollView.onLoadComplete();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPTLHorizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPTLHorizontalScrollView.onLoadComplete();
            }
        }, 2000);
    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPTLHorizontalScrollView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontalscrollview);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.demo_6_title);

        mPTLHorizontalScrollView = (PTLHorizontalScrollView) findViewById(R.id.horizontalscrollview);
        if(mPTLHorizontalScrollView != null){
            mPTLHorizontalScrollView.setOnPullToLoadListener(this);
        }
    }
}
