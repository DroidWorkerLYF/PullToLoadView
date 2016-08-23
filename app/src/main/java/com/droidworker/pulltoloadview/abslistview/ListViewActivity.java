package com.droidworker.pulltoloadview.abslistview;

import com.droidworker.extend.abslistview.PullToLoadListView;
import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.pulltoloadview.BaseActivity;
import com.droidworker.pulltoloadview.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class ListViewActivity extends BaseActivity {
    private PullToLoadListView mPullToLoadListView;
    private Adapter mAdapter;

    @Override
    public void onLoadNew() {
        mPullToLoadListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadListView.onLoadComplete();
                mAdapter.restoreCount();
                mAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPullToLoadListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadListView.onLoadComplete();
                if (mAdapter.getCount() >= 30) {
                    mPullToLoadListView.onAllLoaded();
                } else {
                    mAdapter.updateCount();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, 2000);
    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPullToLoadListView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.demo_2_title);

        mPullToLoadListView = (PullToLoadListView) findViewById(R.id.list_view);

        mAdapter = new Adapter(getResources().getStringArray(R.array.title),
                getResources().getStringArray(R.array.content));
        if (mPullToLoadListView != null) {
            mPullToLoadListView.setMode(LoadMode.PULL_FROM_START_AUTO_LOAD_MORE);
            mPullToLoadListView.setOnPullToLoadListener(this);
            mPullToLoadListView.setAdapter(mAdapter);
        }
    }
}
