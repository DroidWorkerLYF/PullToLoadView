package com.droidworker.example.scrollview;

import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.impl.scrollview.PTLNestedScrollView;
import com.droidworker.example.BaseActivity;
import com.droidworker.example.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class ScrollViewActivity extends BaseActivity {
    private PTLNestedScrollView mPTLNestedScrollView;

    @Override
    public void onLoadNew() {
        mPTLNestedScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPTLNestedScrollView.onLoadComplete();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPTLNestedScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPTLNestedScrollView.onLoadComplete();
            }
        }, 2000);
    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPTLNestedScrollView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.demo_5_title);

        mPTLNestedScrollView = (PTLNestedScrollView) findViewById(R.id.scrollview);

        if(mPTLNestedScrollView != null){
            mPTLNestedScrollView.setOnPullToLoadListener(this);
        }
    }
}
