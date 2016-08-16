package com.droidworker.pulltoloadview;

import com.droidworker.lib.PullToLoadListener;
import com.droidworker.lib.impl.PullToLoadRecyclerView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements PullToLoadListener {
    private Toolbar mToolbar;
    private PullToLoadRecyclerView mPullToLoadRecyclerView;
    private Adapter mAdapter;

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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        mPullToLoadRecyclerView = (PullToLoadRecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new Adapter();
        if(mPullToLoadRecyclerView != null){
            mPullToLoadRecyclerView.setOnPullToLoadListener(this);
            mPullToLoadRecyclerView.getContentView().setAdapter(mAdapter);
        }
    }
}
