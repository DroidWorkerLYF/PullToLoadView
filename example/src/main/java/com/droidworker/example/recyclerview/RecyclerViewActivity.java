package com.droidworker.example.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.droidworker.example.BaseActivity;
import com.droidworker.example.ConditionType;
import com.droidworker.example.DividerItemDecoration;
import com.droidworker.example.R;
import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.constant.LoadMode;
import com.droidworker.pulltoloadview.impl.recyclerview.PullToLoadVerticalRecyclerView;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class RecyclerViewActivity extends BaseActivity {
    private PullToLoadVerticalRecyclerView mPullToLoadVerticalRecyclerView;
    private Adapter mAdapter;
    private int scrollY;

    @Override
    public void onLoadNew() {
        mPullToLoadVerticalRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadVerticalRecyclerView.onLoadComplete();
                mAdapter.restoreCount();
                mPullToLoadVerticalRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPullToLoadVerticalRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadVerticalRecyclerView.onLoadComplete();
                if (mAdapter.getItemCount() >= 30) {
                    mPullToLoadVerticalRecyclerView.setAllLoaded(true);
                } else {
                    mAdapter.updateCount();
                    mPullToLoadVerticalRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }, 2000);
    }

    @Override
    protected PullToLoadBaseView getPullToLoadView() {
        return mPullToLoadVerticalRecyclerView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_v);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.demo_1_title);

        mPullToLoadVerticalRecyclerView = (PullToLoadVerticalRecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new Adapter(getResources().getStringArray(R.array.title),
                getResources().getStringArray(R.array.content), true);
        if (mPullToLoadVerticalRecyclerView != null) {
            mPullToLoadVerticalRecyclerView.setMode(LoadMode.PULL_FROM_START_AUTO_LOAD_MORE);
            mPullToLoadVerticalRecyclerView.setOnPullToLoadListener(this);
            mPullToLoadVerticalRecyclerView.setAdapter(mAdapter);
            mPullToLoadVerticalRecyclerView.addItemDecoration(new DividerItemDecoration(
                    DividerItemDecoration.VERTICAL_LIST, Color.TRANSPARENT,
                    getResources().getDimensionPixelSize(R.dimen.item_divider)));
            addEmptyView(LayoutInflater.from(this).inflate(R.layout.layout_empty, null, false));
            addErrorView(LayoutInflater.from(this).inflate(R.layout.layout_error, null, false));

            mPullToLoadVerticalRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    final int height = mPullToLoadVerticalRecyclerView.getBarHeight() * 2;
                    scrollY += dy;
                    if (scrollY >= height) {
                        toolbar.setAlpha(0.8f);
                    } else {
                        toolbar.setAlpha(1 - 0.2f * scrollY / height);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_empty:
                mPullToLoadVerticalRecyclerView.showConditionView(ConditionType.EMPTY);
                break;
            case R.id.show_error:
                mPullToLoadVerticalRecyclerView.showConditionView(ConditionType.ERROR);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        mAdapter.clear();
        mPullToLoadVerticalRecyclerView.getAdapter().notifyDataSetChanged();
        return true;
    }
}
