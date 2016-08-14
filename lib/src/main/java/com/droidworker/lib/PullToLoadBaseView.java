package com.droidworker.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadBaseView<T extends View> extends LinearLayout implements IPullToLoad<T>{
    public PullToLoadBaseView(Context context) {
        this(context, null);
    }

    public PullToLoadBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setMode(LoadMode loadMode) {

    }

    @Override
    public LoadMode getMode() {
        return null;
    }

    @Override
    public T getContentView() {
        return null;
    }

    @Override
    public boolean canScrollVertical() {
        return false;
    }

    @Override
    public boolean canScrollHorizontal() {
        return false;
    }

    @Override
    public int getHeaderSize() {
        return 0;
    }

    @Override
    public int getFooterSize() {
        return 0;
    }

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public void setLoading() {

    }

    @Override
    public void onLoadComplete() {

    }

    @Override
    public void setOnRefreshListener() {

    }

    @Override
    public void onPull(State state, int distance) {

    }
}
