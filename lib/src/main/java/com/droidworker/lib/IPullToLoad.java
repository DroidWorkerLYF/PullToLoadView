package com.droidworker.lib;

import android.view.View;

import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.constant.State;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public interface IPullToLoad<T extends View> {

    void setMode(LoadMode loadMode);

    LoadMode getMode();

    T getContentView();

    boolean canScrollVertical(Direction direction);

    boolean canScrollHorizontal(Direction direction);

    boolean isLoading();

    void setLoading();

    void onLoadComplete();

    void onAllLoaded();

    void setOnPullToLoadListener(PullToLoadListener pullToLoadListener);

    void onPull(State state, float distance);
}
