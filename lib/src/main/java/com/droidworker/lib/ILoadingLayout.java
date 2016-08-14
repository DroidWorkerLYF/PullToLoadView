package com.droidworker.lib;

import com.droidworker.lib.constant.State;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public interface ILoadingLayout {

    int getSize();

    void setLabel(String label);

    void onPull(State state, int distance);
}
