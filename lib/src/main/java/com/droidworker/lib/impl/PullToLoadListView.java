package com.droidworker.lib.impl;

import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.constant.Orientation;
import com.droidworker.lib.constant.State;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * 扩展ListView
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadListView extends PullToLoadAbsListView<ListView> {

    public PullToLoadListView(Context context) {
        super(context);
    }

    public PullToLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Orientation getScrollOrientation() {
        return Orientation.VERTICAL;
    }

    @Override
    protected ListView createContentView(int layoutId) {
        ListView listView;
        if (layoutId == 0) {
            listView = new ListView(getContext());
        } else {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
            if (view instanceof ListView) {
                listView = (ListView) view;
            } else {
                throw new UnsupportedOperationException("View should be a ListView");
            }
        }
        return listView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible
                && !isAllLoaded() && getMode() == LoadMode.PULL_FROM_START_AUTO_LOAD_MORE) {
            if (mContentView.getFooterViewsCount() == 0) {
                mContentView.addFooterView(mAutoLoadFooter);
            }
            mContentView.smoothScrollToPosition(mContentView.getAdapter().getCount());
            mAutoLoadFooter.onPull(State.LOADING, 0);
            setCurLoadMode(LoadMode.PULL_FROM_END);
            setState(State.LOADING);
        }

        super.onScrollStateChanged(view, scrollState);
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
        mLastItemVisible = false;
        mContentView.removeFooterView(mAutoLoadFooter);
    }

    public void setAdapter(BaseAdapter adapter) {
        mContentView.setAdapter(adapter);
    }
}
