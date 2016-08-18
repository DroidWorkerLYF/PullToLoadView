package com.droidworker.pulltoloadview.recyclerview;

import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.impl.PullToLoadRecyclerView;
import com.droidworker.pulltoloadview.BaseActivity;
import com.droidworker.pulltoloadview.DividerItemDecoration;
import com.droidworker.pulltoloadview.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class RecyclerViewActivity extends BaseActivity {
    private Toolbar mToolbar;
    private PullToLoadRecyclerView mPullToLoadRecyclerView;
    private Adapter mAdapter;
    private int scrollY;

    @Override
    public void onLoadNew() {
        mPullToLoadRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadRecyclerView.onLoadComplete();
                mAdapter.restoreCount();
                mPullToLoadRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPullToLoadRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadRecyclerView.onLoadComplete();
                if (mAdapter.getItemCount() >= 30) {
                    mPullToLoadRecyclerView.onAllLoaded();
                } else {
                    mAdapter.updateCount();
                    mPullToLoadRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }, 2000);
    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPullToLoadRecyclerView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.demo_1_title);

        mPullToLoadRecyclerView = (PullToLoadRecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new Adapter(getResources().getStringArray(R.array.title),
                getResources().getStringArray(R.array.content));
        if (mPullToLoadRecyclerView != null) {
            mPullToLoadRecyclerView.setMode(LoadMode.BOTH);
            mPullToLoadRecyclerView.setOnPullToLoadListener(this);
            mPullToLoadRecyclerView.setAdapter(mAdapter);
            mPullToLoadRecyclerView.addItemDecoration(new DividerItemDecoration(
                    DividerItemDecoration.VERTICAL_LIST, Color.TRANSPARENT,
                    getResources().getDimensionPixelSize(R.dimen.item_divider)));

            mPullToLoadRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    final int height = mPullToLoadRecyclerView.getActionBarHeight() * 2;
                    scrollY += dy;
                    if (scrollY >= height) {
                        mToolbar.setAlpha(0.8f);
                    } else {
                        mToolbar.setAlpha(1 - 0.2f * scrollY / height);
                    }
                }
            });
        }
    }
}
