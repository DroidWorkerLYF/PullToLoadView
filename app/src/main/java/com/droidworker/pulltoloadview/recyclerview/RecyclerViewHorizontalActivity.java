package com.droidworker.pulltoloadview.recyclerview;

import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.impl.PullToLoadHorizontalRecyclerView;
import com.droidworker.pulltoloadview.BaseActivity;
import com.droidworker.pulltoloadview.DividerItemDecoration;
import com.droidworker.pulltoloadview.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class RecyclerViewHorizontalActivity extends BaseActivity {
    private PullToLoadHorizontalRecyclerView mPullToLoadHorizontalRecyclerView;
    private Adapter mAdapter;

    @Override
    public void onLoadNew() {
        mPullToLoadHorizontalRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadHorizontalRecyclerView.onLoadComplete();
                mAdapter.restoreCount();
                mPullToLoadHorizontalRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPullToLoadHorizontalRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadHorizontalRecyclerView.onLoadComplete();
                if (mAdapter.getItemCount() >= 30) {
                    mPullToLoadHorizontalRecyclerView.onAllLoaded();
                } else {
                    mAdapter.updateCount();
                    mPullToLoadHorizontalRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }, 2000);
    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPullToLoadHorizontalRecyclerView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recyclerview_h);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.demo_3_title);

        mPullToLoadHorizontalRecyclerView = (PullToLoadHorizontalRecyclerView) findViewById(
                R.id.recycler_view_h);
        mAdapter = new Adapter(getResources().getStringArray(R.array.title),
                getResources().getStringArray(R.array.content), false);
        if (mPullToLoadHorizontalRecyclerView != null) {
            mPullToLoadHorizontalRecyclerView.setMode(LoadMode.BOTH);
            mPullToLoadHorizontalRecyclerView.setOnPullToLoadListener(this);
            mPullToLoadHorizontalRecyclerView.setAdapter(mAdapter);
            mPullToLoadHorizontalRecyclerView.addItemDecoration(new DividerItemDecoration(
                    DividerItemDecoration.HORIZONTAL_LIST, Color.TRANSPARENT,
                    getResources().getDimensionPixelSize(R.dimen.item_divider)));
        }
    }
}
