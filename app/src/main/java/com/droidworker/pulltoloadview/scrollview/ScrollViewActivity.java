package com.droidworker.pulltoloadview.scrollview;

import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.impl.scrollview.PullToLoadScrollView;
import com.droidworker.pulltoloadview.BaseActivity;
import com.droidworker.pulltoloadview.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class ScrollViewActivity extends BaseActivity {
    private PullToLoadScrollView mPullToLoadScrollView;

    @Override
    public void onLoadNew() {
        mPullToLoadScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadScrollView.onLoadComplete();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPullToLoadScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadScrollView.onLoadComplete();
            }
        }, 2000);
    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPullToLoadScrollView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.demo_5_title);

        mPullToLoadScrollView = (PullToLoadScrollView) findViewById(R.id.scrollview);

        if(mPullToLoadScrollView != null){
            mPullToLoadScrollView.setOnPullToLoadListener(this);
        }
    }
}
