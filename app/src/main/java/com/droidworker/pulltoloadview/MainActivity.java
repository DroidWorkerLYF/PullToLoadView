package com.droidworker.pulltoloadview;

import com.droidworker.lib.PullToLoadListener;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.impl.PullToLoadRecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 效果测试页面
 * @author https://github.com/DroidWorkerLYF
 */
public class MainActivity extends AppCompatActivity implements PullToLoadListener {
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
                if (mAdapter.getItemCount() >= 60) {
                    mPullToLoadRecyclerView.onAllLoaded();
                } else {
                    mAdapter.updateCount();
                    mPullToLoadRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        mPullToLoadRecyclerView = (PullToLoadRecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new Adapter(getResources().getStringArray(R.array.title),
                getResources().getStringArray(R.array.content));
        if (mPullToLoadRecyclerView != null) {
            mPullToLoadRecyclerView.setMode(LoadMode.BOTH);
            mPullToLoadRecyclerView.setOnPullToLoadListener(this);
            mPullToLoadRecyclerView.setAdapter(mAdapter);
            mPullToLoadRecyclerView.getContentView()
                    .addItemDecoration(new DividerItemDecoration(
                            DividerItemDecoration.VERTICAL_LIST, Color.TRANSPARENT,
                            getResources().getDimensionPixelSize(R.dimen.item_divider)));

            mPullToLoadRecyclerView.getContentView()
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.refresh: {
            mPullToLoadRecyclerView.setLoading();
        }
            break;
        case R.id.pull_from_start: {
            mPullToLoadRecyclerView.setMode(LoadMode.PULL_FROM_START);
        }
            break;
        case R.id.pull_from_start_auto_load_more: {
            mPullToLoadRecyclerView.setMode(LoadMode.PULL_FROM_START_AUTO_LOAD_MORE);
        }
            break;
        case R.id.pull_from_end: {
            mPullToLoadRecyclerView.setMode(LoadMode.PULL_FROM_END);
        }
            break;
        case R.id.both: {
            mPullToLoadRecyclerView.setMode(LoadMode.BOTH);
        }
            break;
        case R.id.disabled: {
            mPullToLoadRecyclerView.setMode(LoadMode.DISABLED);
        }
            break;
        case R.id.manual_only: {
            mPullToLoadRecyclerView.setMode(LoadMode.MANUAL_ONLY);
        }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
