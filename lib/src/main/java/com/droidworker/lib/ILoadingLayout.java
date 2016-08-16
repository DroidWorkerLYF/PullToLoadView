package com.droidworker.lib;

import com.droidworker.lib.constant.State;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public interface ILoadingLayout {

    int getSize();

    void onPull(State state, float distance);
}
