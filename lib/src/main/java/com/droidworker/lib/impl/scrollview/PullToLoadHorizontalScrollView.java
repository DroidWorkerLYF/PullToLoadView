package com.droidworker.lib.impl.scrollview;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.Orientation;
import com.droidworker.lib.impl.LoadingLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 扩展{@link HorizontalScrollView}
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
        switch (direction) {
        case START:
        default:
            return mContentView.getScrollX() != 0;
        case END:
            View scrollViewChild = mContentView.getChildAt(0);
            if (scrollViewChild != null) {
                return mContentView.getScrollX() < (scrollViewChild.getWidth() - getWidth());
            }
            return true;
        }
    }

    @Override
    protected Orientation getScrollOrientation() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected ILoadingLayout createHeader() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected ILoadingLayout createFooter() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected HorizontalScrollView createContentView(int layoutId) {
        HorizontalScrollView horizontalScrollView;
        if (layoutId == 0) {
            horizontalScrollView = new HorizontalScrollView(getContext());
        } else {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, mContentView, false);
            if (view instanceof HorizontalScrollView) {
                horizontalScrollView = (HorizontalScrollView) view;
            } else {
                throw new UnsupportedOperationException("View should be a HorizontalScrollView");
            }
        }
        return horizontalScrollView;
    }

    @Override
    protected void updateContentUI(boolean isUnderBar) {

    }
}
