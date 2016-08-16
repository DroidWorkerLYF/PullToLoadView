package com.droidworker.lib.impl;

import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class PullToLoadAbsListView extends PullToLoadBaseView<AbsListView>
        implements AbsListView.OnScrollListener {
    private boolean mLastItemVisible;
    private AbsListView.OnScrollListener mOnScrollListener;

    public PullToLoadAbsListView(Context context) {
        this(context, null);

    }

    public PullToLoadAbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContentView.setOnScrollListener(this);
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    @Override
    public boolean canScrollVertical(Direction direction) {
        switch (direction) {
        case START:
        default: {
            return isFirstItemVisible();
        }
        case END: {
            return isLastItemVisible();
        }
        }
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        return false;
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
    protected void updateContentUI(boolean isUnderBar) {
        if(getMode() == LoadMode.PULL_FROM_START_AUTO_LOAD){

        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible) {

        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    private boolean isFirstItemVisible() {
        final Adapter adapter = mContentView.getAdapter();

        if (adapter == null || adapter.isEmpty()) {
            return true;
        } else {
            if (mContentView.getFirstVisiblePosition() == 0) {
                final View firstVisibleChild = mContentView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mContentView.getTop();
                }
            }
        }
        return false;
    }

    private boolean isLastItemVisible() {
        final Adapter adapter = mContentView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {
            final int lastItemPosition = mContentView.getCount() - 1;
            final int lastVisiblePosition = mContentView.getLastVisiblePosition();

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - mContentView.getFirstVisiblePosition();
                final View lastVisibleChild = mContentView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mContentView.getBottom();
                }
            }
        }

        return false;
    }
}
