package com.droidworker.lib.impl;

import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.Orientation;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadRecyclerView extends PullToLoadBaseView<RecyclerView> {
    private static final String TAG = "PullToLoadRecyclerView";

    public PullToLoadRecyclerView(Context context) {
        super(context);
    }

    public PullToLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertical(Direction direction) {
        return ViewCompat.canScrollVertically(getContentView(), direction.getIntValue());
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        return ViewCompat.canScrollHorizontally(getContentView(), direction.getIntValue());
    }

    @Override
    protected Orientation getScrollOrientation() {
        RecyclerView.LayoutManager layoutManager = getContentView().getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            switch (((LinearLayoutManager) layoutManager).getOrientation()) {
            case LinearLayoutManager.HORIZONTAL:
                return Orientation.HORIZONTAL;
            case LinearLayoutManager.VERTICAL:
            default:
                return Orientation.VERTICAL;
            }
        }
        // 如果自己实现了一个LayoutManager,那么要另做处理
        return Orientation.VERTICAL;
    }

    @Override
    protected LoadingLayout createHeader() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected LoadingLayout createFooter() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected RecyclerView createContentView(int layoutId) {
        RecyclerView recyclerView;
        if (layoutId == 0) {
            recyclerView = new RecyclerView(getContext());
        } else {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
            if (view instanceof RecyclerView) {
                recyclerView = (RecyclerView) view;
            } else {
                throw new UnsupportedOperationException("View should be a RecyclerView");
            }
        }
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        return recyclerView;
    }

    @Override
    protected void updateContentUI(boolean isUnderBar) {
        mContentView.scrollToPosition(0);
    }
}
