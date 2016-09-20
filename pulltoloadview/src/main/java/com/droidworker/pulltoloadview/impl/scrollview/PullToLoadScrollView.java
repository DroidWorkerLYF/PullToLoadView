package com.droidworker.pulltoloadview.impl.scrollview;

import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.constant.Direction;
import com.droidworker.pulltoloadview.constant.LoadMode;
import com.droidworker.pulltoloadview.constant.Orientation;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 扩展{@link NestedScrollView}
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadScrollView extends PullToLoadBaseView<NestedScrollView> {
    public PullToLoadScrollView(Context context) {
        super(context);
    }

    public PullToLoadScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertical(Direction direction) {
        // 参照ViewCompat中的方法
        final int offset = mContentView.computeVerticalScrollOffset();
        final int range = mContentView.computeVerticalScrollRange()
                - mContentView.computeVerticalScrollExtent();
        if (range == 0)
            return false;
        if (direction.getIntValue() < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        return false;
    }

    @Override
    protected Orientation getScrollOrientation() {
        return Orientation.VERTICAL;
    }

    @Override
    protected NestedScrollView createContentView(int layoutId) {
        NestedScrollView scrollView;
        if (layoutId == 0) {
            scrollView = new NestedScrollView(getContext());
        } else {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, mContentView, false);
            if (view instanceof NestedScrollView) {
                scrollView = (NestedScrollView) view;
            } else {
                throw new UnsupportedOperationException("View should be a NestedScrollView");
            }
        }
        return scrollView;
    }

    @Override
    protected void updateContentUI(boolean isUnderBar) {

    }
}
