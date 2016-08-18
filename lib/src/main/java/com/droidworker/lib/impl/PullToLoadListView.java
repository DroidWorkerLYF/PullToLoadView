package com.droidworker.lib.impl;

import com.droidworker.lib.constant.Orientation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        return listView;
    }

    public void setAdapter(BaseAdapter adapter) {
        mContentView.setAdapter(adapter);
    }
}
