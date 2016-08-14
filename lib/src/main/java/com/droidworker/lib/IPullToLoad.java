package com.droidworker.lib;

import android.view.View;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public interface IPullToLoad<T extends View> {

    void setMode(LoadMode loadMode);

    LoadMode getMode();

    T getContentView();

    boolean canScrollVertical();

    boolean canScrollHorizontal();

    int getHeaderSize();

    int getFooterSize();

    boolean isLoading();

    void setLoading();

    void onLoadComplete();

    void setOnRefreshListener();

    void onPull(State state, int distance);
}
