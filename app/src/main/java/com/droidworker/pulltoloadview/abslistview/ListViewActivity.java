package com.droidworker.pulltoloadview.abslistview;

import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.impl.PullToLoadListView;
import com.droidworker.pulltoloadview.BaseActivity;
import com.droidworker.pulltoloadview.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.AbsListView;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class ListViewActivity extends BaseActivity {
    private Toolbar mToolbar;
    private PullToLoadListView mPullToLoadListView;
    private Adapter mAdapter;
    private int scrollY;

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
                if(mAdapter.getCount() >= 30){
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

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.demo_2_title);

        mPullToLoadListView = (PullToLoadListView) findViewById(R.id.list_view);

        mAdapter = new Adapter(getResources().getStringArray(R.array.title),
                getResources().getStringArray(R.array.content));
        if(mPullToLoadListView != null){
            mPullToLoadListView.setMode(LoadMode.BOTH);
            mPullToLoadListView.setOnPullToLoadListener(this);
            mPullToLoadListView.setAdapter(mAdapter);
//            mPullToLoadListView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.item_divider), 0, 0);

            mPullToLoadListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }
    }
}
