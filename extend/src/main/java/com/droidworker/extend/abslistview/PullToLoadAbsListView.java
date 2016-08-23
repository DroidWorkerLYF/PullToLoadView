package com.droidworker.extend.abslistview;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.impl.LoadingLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;

/**
 * 扩展AbsListView.
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class PullToLoadAbsListView<T extends AbsListView> extends PullToLoadBaseView<T>
        implements AbsListView.OnScrollListener {
    protected boolean mLastItemVisible;
    /**
     * 自动加载更多时添加到最后的footer
     */
    protected LoadingLayout mAutoLoadFooter;
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
            return !isFirstItemVisible();
        }
        case END: {
            return !isLastItemVisible();
        }
        }
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        return false;
    }

    @Override
    protected ILoadingLayout createHeader() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected ILoadingLayout createFooter() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected void updateContentUI(boolean isUnderBar) {
        post(new Runnable() {
            @Override
            public void run() {
                mContentView.setSelection(0);
            }
        });
        if (getMode() == LoadMode.PULL_FROM_START_AUTO_LOAD_MORE) {
            mAutoLoadFooter = new LoadingLayout(getContext(), getScrollOrientation());
            AbsListView.LayoutParams layoutParams;
            switch (getScrollOrientation()) {
            case VERTICAL:
            default: {
                layoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
            }
                break;
            case HORIZONTAL: {
                layoutParams = new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT);
            }
                break;
            }
            mAutoLoadFooter.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        mLastItemVisible = (totalItemCount > 0)
                && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
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
            final int lastItemPosition = mContentView.getAdapter().getCount() - 1;
            final int lastVisiblePosition = mContentView.getLastVisiblePosition();

            if (lastVisiblePosition >= lastItemPosition) {
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