package com.droidworker.lib.impl;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.constant.Orientation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

/**
 * WebView的扩展
 * @author https://github.com/DroidWorkerLYF
 */
public class PullToLoadWebView extends PullToLoadBaseView<WebView> {

    public PullToLoadWebView(Context context) {
        this(context, null);
    }

    public PullToLoadWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setMode(LoadMode.PULL_FROM_START);
    }

    @Override
    public boolean canScrollVertical(Direction direction) {
        switch (direction) {
        case START:
        default: {
            return mContentView.getScrollY() != 0;
        }
        case END: {
            return true;
        }
        }
    }

    @Override
    public boolean canScrollHorizontal(Direction direction) {
        return true;
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
    protected WebView createContentView(int layoutId) {
        WebView webView;
        if (layoutId == 0) {
            webView = new WebView(getContext());
        } else {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, mContentView, false);
            if (view instanceof WebView) {
                webView = (WebView) view;
            } else {
                throw new UnsupportedOperationException("View should be a WebView");
            }
        }
        return webView;
    }

    @Override
    protected void updateContentUI(boolean isUnderBar) {

    }
}
