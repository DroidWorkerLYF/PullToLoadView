package com.droidworker.pulltoloadview.impl.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.constant.Direction;
import com.droidworker.pulltoloadview.constant.Orientation;

/**
 * 扩展{@link HorizontalScrollView}
 * @author https://github.com/DroidWorkerLYF
 */
public class PTLHorizontalScrollView extends PullToLoadBaseView<HorizontalScrollView> {
    public PTLHorizontalScrollView(Context context) {
        super(context);
    }

    public PTLHorizontalScrollView(Context context, AttributeSet attrs) {
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
                return scrollViewChild == null || mContentView.getScrollX() < (scrollViewChild.getWidth() - getWidth());
        }
    }

    @Override
    protected Orientation getScrollOrientation() {
        return Orientation.HORIZONTAL;
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
