package com.droidworker.lib.impl;

import com.droidworker.lib.constant.Orientation;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadHorizontalRecyclerView extends PullToLoadRecyclerView {

    public PullToLoadHorizontalRecyclerView(Context context) {
        super(context);
    }

    public PullToLoadHorizontalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Orientation getScrollOrientation() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected RecyclerView createContentView(int layoutId) {
        RecyclerView recyclerView = super.createContentView(layoutId);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        return recyclerView;
    }
}
