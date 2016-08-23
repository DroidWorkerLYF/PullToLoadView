package com.droidworker.lib.impl.scrollview;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.Orientation;
import com.droidworker.lib.impl.LoadingLayout;

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
        switch (direction) {
        case START:
        default:
            return mContentView.getScrollY() != 0;
        case END:
            View scrollViewChild = mContentView.getChildAt(0);
            if (scrollViewChild != null) {
                return mContentView.getScrollY() < (scrollViewChild.getHeight() - getHeight());
            }
            return true;
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
    protected ILoadingLayout createHeader() {
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    @Override
    protected ILoadingLayout createFooter() {
        return new LoadingLayout(getContext(), getScrollOrientation());
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
