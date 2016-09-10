package com.droidworker.pulltoloadview.impl.recyclerview;

import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.constant.Direction;
import com.droidworker.pulltoloadview.constant.LoadMode;
import com.droidworker.pulltoloadview.constant.State;
import com.droidworker.pulltoloadview.impl.LoadingLayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 支持加载更新,加载更多的RecyclerView扩展.
 * 因为RecyclerView方向的不确定性,指定为{@link android.support.v7.widget.LinearLayoutManager}时,
 * 默认是垂直的,无法指定水平方向,所以目前还是拆分成独立的竖向和横向由子类去实现
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class PullToLoadRecyclerView extends PullToLoadBaseView<RecyclerView> {
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

        // 设置滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && loadMore && !isAllLoaded()) {
                    HeaderAndFooterWrapper wrapper = getAdapter();
                    if (wrapper != null && mAutoLoadFooter != null) {
                        recyclerView.scrollToPosition(wrapper.getItemCount());
                        if (!wrapper.containsFooter(mAutoLoadFooter)) {
                            wrapper.addFooter(mAutoLoadFooter);
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
        } else {
            mAutoLoadFooter = null;
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
        HeaderAndFooterWrapper wrapper = getAdapter();
        if (wrapper != null && mAutoLoadFooter != null && wrapper.containsFooter(mAutoLoadFooter)) {
            wrapper.removeFooter(mAutoLoadFooter);
            wrapper.notifyDataSetChanged();
        }
    }

    /**
     * 设置Adapter,将参数包装为HeaderAndFooterWrapper用于添加header和footer
     * @param adapter adapter
     */
    public void setAdapter(@NonNull RecyclerView.Adapter adapter) {
        final HeaderAndFooterWrapper wrapper = new HeaderAndFooterWrapper(adapter);
        mContentView.setAdapter(wrapper);
    }

    /**
     * 获取包装后的Adapter
     * @return {@link HeaderAndFooterWrapper}
     */
    public HeaderAndFooterWrapper getAdapter() {
        return (HeaderAndFooterWrapper) mContentView.getAdapter();
    }

    /**
     * 获取被包装的Adapter
     * @return {@link android.support.v7.widget.RecyclerView.Adapter}
     */
    public RecyclerView.Adapter getWrappedAdapter() {
        return ((HeaderAndFooterWrapper) mContentView.getAdapter()).getWrappedAdapter();
    }

    /**
     * 设置滚动监听
     * @param onScrollListener listener
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    /**
     * 添加{@link android.support.v7.widget.RecyclerView.ItemDecoration}
     * @param decor item decoration
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mContentView.addItemDecoration(decor);
    }

}
