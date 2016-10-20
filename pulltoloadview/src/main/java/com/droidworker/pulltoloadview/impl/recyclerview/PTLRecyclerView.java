package com.droidworker.pulltoloadview.impl.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.constant.Direction;
import com.droidworker.pulltoloadview.constant.LoadMode;
import com.droidworker.pulltoloadview.constant.State;
import com.droidworker.pulltoloadview.impl.LoadingLayout;

/**
 * 支持加载更新,加载更多的RecyclerView扩展.
 * 因为RecyclerView方向的不确定性,指定为{@link android.support.v7.widget.LinearLayoutManager}时,
 * 默认是垂直的,无法指定水平方向,所以目前还是拆分成独立的竖向和横向由子类去实现
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class PTLRecyclerView extends PullToLoadBaseView<RecyclerView> {
    private final static int EMPTY = -1;
    private HeaderAndFooterWrapper mWrapper = new HeaderAndFooterWrapper();
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
    private InternalObserver mInternalObserver = new InternalObserver();

    public PTLRecyclerView(Context context) {
        super(context);
    }

    public PTLRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertical(Direction direction) {
        return internalCanScrollVertical(direction, false);
    }

    /**
     * 是否可以滚动和load more的判断都需要使用此方法,通过是不是要提前加载来区分返回状态.
     * @param direction 滚动方向
     * @param earlyLoading 是否提前加载
     */
    private boolean internalCanScrollVertical(Direction direction, boolean earlyLoading){
        // 参照ViewCompat中的方法
        final int offset = mContentView.computeVerticalScrollOffset();
        final int range = mContentView.computeVerticalScrollRange()
                - mContentView.computeVerticalScrollExtent();
        if (range == 0) {
            return false;
        }
        if (direction.getIntValue() < 0) {
            return offset > 0;
        } else {
            LoadMode loadMode = getMode();
            if (loadMode.isAutoLoadMore()) {
                if (loadMode.shouldShowAutoLoadMoreFooter()) {
                    return offset < range - mAutoLoadFooter.getHeight();
                } else {
                    View view = findLastVisibleItem();
                    if (view == null || !earlyLoading) {
                        return offset < range - 1;
                    }
                    return offset + view.getHeight() < range;
                }
            } else {
                return offset < range - 1;
            }
        }
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        return internalCanScrollHorizontal(direction, false);
    }

    private boolean internalCanScrollHorizontal(Direction direction, boolean earlyLoading){
        final int offset = mContentView.computeHorizontalScrollOffset();
        final int range = mContentView.computeHorizontalScrollRange()
                - mContentView.computeHorizontalScrollExtent();
        if (range == 0){
            return false;
        }
        if (direction.getIntValue() < 0) {
            return offset > 0;
        } else {
            LoadMode loadMode = getMode();
            if (loadMode.isAutoLoadMore()) {
                if(loadMode.shouldShowAutoLoadMoreFooter()){
                    return offset < range - mAutoLoadFooter.getWidth();
                } else {
                    View view = findLastVisibleItem();
                    if (view == null || !earlyLoading) {
                        return offset < range - 1;
                    }
                    return offset + view.getWidth() < range ;
                }
            } else {
                return offset < range - 1;
            }
        }
    }

    private View findLastVisibleItem() {
        RecyclerView.LayoutManager layoutManager = mContentView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            if (position >= 0) {
                return layoutManager.findViewByPosition(position);
            }
        }
        return null;
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
                    if (getMode().isAutoLoadMore()) {
                        setCurLoadMode(LoadMode.END);
                        if(mAutoLoadFooter != null){
                            mAutoLoadFooter.onPull(State.LOADING, 0);
                        }
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

                switch (getScrollOrientation()) {
                    case VERTICAL:
                    default:
                        loadMore = !internalCanScrollVertical(Direction.END, true);
                        break;
                    case HORIZONTAL:
                        loadMore = !internalCanScrollHorizontal(Direction.END, true);
                        break;
                }

                loadMore = loadMore && getMode().isAutoLoadMore() &&
                        mWrapper.getWrappedItemCount() > 0;

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
        if (getMode().shouldShowAutoLoadMoreFooter()) {
            if (mAutoLoadFooter == null) {
                mAutoLoadFooter = new LoadingLayout(getContext(), getScrollOrientation());
                RecyclerView.LayoutParams layoutParams;
                switch (getScrollOrientation()) {
                case VERTICAL:
                default:
                    layoutParams = new RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    break;
                case HORIZONTAL:
                    layoutParams = new RecyclerView.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.MATCH_PARENT);
                    break;
                }
                mAutoLoadFooter.setLayoutParams(layoutParams);
            }
            addLoadingFooter();
        } else {
            removeLoadingFooter();
            mAutoLoadFooter = null;
        }
    }

    /**
     * 添加自动加载更多的footer
     */
    private void addLoadingFooter() {
        if (mWrapper != null && !mWrapper.containsFooter(mAutoLoadFooter)) {
            mWrapper.addFooter(mAutoLoadFooter);
        }
    }

    /**
     * 移除自动加载更多的footer,只使用于非{@link LoadMode#START_AUTO_LOAD_MORE_WITH_FOOTER}
     * 否则,应该使用{@link #updateFooterHeight(boolean)}
     */
    private void removeLoadingFooter() {
        if (mWrapper != null && mWrapper.containsFooter(mAutoLoadFooter)) {
            mWrapper.removeFooter(mAutoLoadFooter);
        }
    }

    /**
     * 根据是否show出来,设定
     * @param show
     */
    private void updateFooterHeight(boolean show) {
        if (mAutoLoadFooter == null) {
            return;
        }
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) mAutoLoadFooter
                .getLayoutParams();
        switch (getScrollOrientation()) {
        case VERTICAL:
        default:
            layoutParams.height = show ? LayoutParams.WRAP_CONTENT : 1;
            break;
        case HORIZONTAL:
            layoutParams.width = show ? LayoutParams.WRAP_CONTENT : 1;
            break;
        }
        mAutoLoadFooter.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mAutoLoadFooter.setLayoutParams(layoutParams);
    }

    @Override
    public void onLoadComplete() {
        if(isUpdating()){
            mContentView.scrollToPosition(0);
        }
        super.onLoadComplete();
    }
    
    @Override
    protected void reset() {
        super.reset();
        loadMore = false;
    }

    @Override
    public void setAllLoaded(boolean isAllLoaded) {
        super.setAllLoaded(isAllLoaded);
        // 需要根据是否是全部加载完毕,更新footer高度
        updateFooterHeight(!isAllLoaded);
    }

    /**
     * 设置Adapter,将参数包装为HeaderAndFooterWrapper用于添加header和footer
     * @param adapter adapter
     */
    public void setAdapter(@NonNull RecyclerView.Adapter adapter) {
        if (mWrapper == null) {
            return;
        }
        RecyclerView.Adapter oldAdapter = getWrappedAdapter();
        if(oldAdapter != null){
            oldAdapter.unregisterAdapterDataObserver(mInternalObserver);
        }
        adapter.registerAdapterDataObserver(mInternalObserver);
        mWrapper.setWrappedAdapter(adapter);
        mContentView.setAdapter(mWrapper);
    }

    /**
     * 获取包装后的Adapter
     * @return {@link HeaderAndFooterWrapper}
     */
    public HeaderAndFooterWrapper getAdapter() {
        return mWrapper;
    }

    /**
     * 获取被包装的Adapter
     * @return {@link android.support.v7.widget.RecyclerView.Adapter}
     */
    public RecyclerView.Adapter getWrappedAdapter() {
        return mWrapper == null ? null : mWrapper.getWrappedAdapter();
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

    public void setEmptyView(View emptyView){
        addConditionViewInternal(emptyView, EMPTY);
    }

    private class InternalObserver extends RecyclerView.AdapterDataObserver{
        @Override
        public void onChanged() {
            super.onChanged();
            if(mWrapper.getWrappedItemCount() == 0){
                showConditionView(EMPTY);
            } else {
                hideConditionView(EMPTY);
            }
        }
    }
}
