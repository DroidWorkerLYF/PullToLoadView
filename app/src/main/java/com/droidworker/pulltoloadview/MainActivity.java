package com.droidworker.pulltoloadview;

import com.droidworker.lib.PullToLoadListener;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.impl.PullToLoadRecyclerView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
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
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mPullToLoadRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToLoadRecyclerView.onLoadComplete();
                mPullToLoadRecyclerView.onAllLoaded();
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
        mAdapter = new Adapter();
        if (mPullToLoadRecyclerView != null) {
            mPullToLoadRecyclerView.setMode(LoadMode.BOTH);
            mPullToLoadRecyclerView.setOnPullToLoadListener(this);
            mPullToLoadRecyclerView.getContentView().setAdapter(mAdapter);

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

        }
        return super.onOptionsItemSelected(item);
    }
}
