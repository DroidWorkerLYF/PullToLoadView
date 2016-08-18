package com.droidworker.lib.impl;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.constant.Orientation;
import com.droidworker.lib.constant.State;
import com.droidworker.lib.recyclerview.BaseRecyclerViewAdapter;
import com.droidworker.lib.recyclerview.HeaderAndFooterWrapper;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 支持加载更新,加载更多的RecyclerView
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadRecyclerView extends PullToLoadBaseView<RecyclerView> {
    /**
     * 自动加载更多时添加到最后的footer
     */
    private LoadingLayout mAutoLoadFooter;
    /**
     * 是否要加载更多
     */
    private boolean loadMore;
    /**
     * 滚动的监听
     */
    private RecyclerView.OnScrollListener mOnScrollListener;

    public PullToLoadRecyclerView(Context context) {
        super(context);
    }

    public PullToLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertical(Direction direction) {
        // 使用ViewCompat中的方法
        return ViewCompat.canScrollVertically(mContentView, direction.getIntValue());
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        // 使用ViewCompat中的方法
        return ViewCompat.canScrollHorizontally(mContentView, direction.getIntValue());
    }

    @Override
    protected Orientation getScrollOrientation() {
        RecyclerView.LayoutManager layoutManager = mContentView.getLayoutManager();
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
    protected ILoadingLayout createHeader() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected ILoadingLayout createFooter() {
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
        // 设置滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && loadMore && !isAllLoaded()) {
                    HeaderAndFooterWrapper wrapper = getAdapter();
                    if (wrapper != null) {
                        recyclerView.scrollToPosition(wrapper.getItemCount());
                        if (!wrapper.containsFooter(mAutoLoadFooter)) {
                            wrapper.addFooter(mAutoLoadFooter);
                            wrapper.notifyDataSetChanged();
                        }
                        mAutoLoadFooter.onPull(State.LOADING, 0);
                        setCurLoadMode(LoadMode.PULL_FROM_END);
                        setState(State.LOADING);
                    }
                }

                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                loadMore = !canScrollVertical(Direction.END)
                        && getMode() == LoadMode.PULL_FROM_START_AUTO_LOAD_MORE;

                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrolled(recyclerView, dx, dy);
                }
            }
        });
        return recyclerView;
    }

    @Override
    protected void updateContentUI(boolean isUnderBar) {
        mContentView.scrollToPosition(0);
        if (getMode() == LoadMode.PULL_FROM_START_AUTO_LOAD_MORE) {
            mAutoLoadFooter = new LoadingLayout(getContext(), getScrollOrientation());
            RecyclerView.LayoutParams layoutParams;
            switch (getScrollOrientation()) {
            case VERTICAL:
            default: {
                layoutParams = new RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
            }
                break;
            case HORIZONTAL: {
                layoutParams = new RecyclerView.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT);
            }
                break;
            }
            mAutoLoadFooter.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onLoading() {
        if (getMode() == LoadMode.PULL_FROM_START_AUTO_LOAD_MORE) {
            if (getCurLoadMode() == LoadMode.PULL_FROM_END) {
                if (getPullToLoadListener() != null) {
                    getPullToLoadListener().onLoadMore();
                }
            } else {
                super.onLoading();
            }
        } else {
            super.onLoading();
        }
    }

    @Override
    protected void reset() {
        super.reset();
        loadMore = false;
        HeaderAndFooterWrapper wrapper = (HeaderAndFooterWrapper) mContentView.getAdapter();
        if (wrapper != null) {
            wrapper.removeFooter(mAutoLoadFooter);
            wrapper.notifyDataSetChanged();
        }
    }

    public void setAdapter(BaseRecyclerViewAdapter baseRecyclerViewAdapter) {
        final HeaderAndFooterWrapper wrapper = new HeaderAndFooterWrapper(baseRecyclerViewAdapter);
        mContentView.setAdapter(wrapper);
    }

    public HeaderAndFooterWrapper getAdapter() {
        return (HeaderAndFooterWrapper) mContentView.getAdapter();
    }

    public BaseRecyclerViewAdapter getWrappedAdapter() {
        return ((HeaderAndFooterWrapper) mContentView.getAdapter()).getWrappedAdapter();
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor){
        mContentView.addItemDecoration(decor);
    }
}
