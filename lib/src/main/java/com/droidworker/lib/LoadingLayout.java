package com.droidworker.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.droidworker.lib.constant.State;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class LoadingLayout extends FrameLayout implements ILoadingLayout {
    public LoadingLayout(Context context) {
        super(context);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void onPull(State state, int distance) {

    }
}
