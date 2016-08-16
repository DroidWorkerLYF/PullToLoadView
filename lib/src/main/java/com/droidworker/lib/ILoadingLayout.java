package com.droidworker.lib;

import com.droidworker.lib.constant.State;

import android.view.View;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public interface ILoadingLayout {

    int getSize();

    void onPull(State state, float distance);

    void show();

    void hide();

    View getLoadingView();
}
