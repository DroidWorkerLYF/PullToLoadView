package com.droidworker.pulltoloadview.impl.recyclerview;

import com.droidworker.pulltoloadview.constant.Orientation;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 垂直方向的RecyclerView,{@link #createContentView(int)}后,如果没有显示的指定布局方向,则默认设置为
 * {@link LinearLayoutManager#VERTICAL}
 * @author https://github.com/DroidWorkerLYF
 */
public class PTLVerticalRecyclerView extends PTLRecyclerView {

    public PTLVerticalRecyclerView(Context context) {
        super(context);
    }

    public PTLVerticalRecyclerView(Context context, AttributeSet attrs) {
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
