package com.droidworker.lib.impl.recyclerview;

import com.droidworker.lib.constant.Orientation;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 支持加载更新,加载更多的RecyclerView
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadVerticalRecyclerView extends PullToLoadRecyclerView {

    public PullToLoadVerticalRecyclerView(Context context) {
        super(context);
    }

    public PullToLoadVerticalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Orientation getScrollOrientation() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createContentView(int layoutId) {
        RecyclerView recyclerView = super.createContentView(layoutId);
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
        return recyclerView;
    }
}
