package com.droidworker.lib.impl.scrollview;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.Orientation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * 扩展HorizontalScrollView
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadHorizontalScrollView extends PullToLoadBaseView<HorizontalScrollView> {
    public PullToLoadHorizontalScrollView(Context context) {
        super(context);
    }

    public PullToLoadHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertical(Direction direction) {
        return false;
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        return false;
    }

    @Override
    protected Orientation getScrollOrientation() {
        return null;
    }

    @Override
    protected ILoadingLayout createHeader() {
        return null;
    }

    @Override
    protected ILoadingLayout createFooter() {
        return null;
    }

    @Override
    protected HorizontalScrollView createContentView(int layoutId) {
        return null;
    }

    @Override
    protected void updateContentUI(boolean isUnderBar) {

    }
}
