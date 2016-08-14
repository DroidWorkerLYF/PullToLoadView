package com.droidworker.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.constant.Orientation;
import com.droidworker.lib.constant.State;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class PullToLoadBaseView<T extends View> extends LinearLayout implements IPullToLoad<T> {
    private LoadingLayout mHeader;
    private LoadingLayout mFooter;
    private T mContentView;
    private boolean mIsUnderBar;
    private LoadMode mLoadMode;
    private LoadMode mCurLoadMode;
    private State mState = State.RESET;
    private boolean mIsIntercepted;
    private int mTouchSlop;
    private float mLastX, mLastY;
    private float mInitialX, mInitialY;

    public PullToLoadBaseView(Context context) {
        this(context, null);
    }

    public PullToLoadBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullToLoadView);
        mIsUnderBar = typedArray.getBoolean(R.styleable.PullToLoadView_underBar, false);
        typedArray.recycle();

        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();

        initView();
    }

    private void initView() {
        switch (getScrollOrientation()) {
            case VERTICAL:
                setOrientation(LinearLayout.VERTICAL);
                break;
            case HORIZONTAL:
                setOrientation(LinearLayout.HORIZONTAL);
                break;
        }

        mContentView = createContentView();
        addContentView(mContentView);
        mHeader = createHeader();
        addHeader(mHeader, mIsUnderBar);
        mFooter = createFooter();
        addViewInternal(mFooter, getLoadingLayoutLayoutParams());
    }

    protected abstract Orientation getScrollOrientation();

    protected abstract LoadingLayout createHeader();

    protected abstract LoadingLayout createFooter();

    protected abstract T createContentView();

    private void addContentView(T contentView) {
        addViewInternal(contentView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    protected final void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
        this.addViewInternal(child, -1, params);
    }

    protected abstract void addHeader(LoadingLayout loadingLayout, boolean isUnderBar);

    private LinearLayout.LayoutParams getLoadingLayoutLayoutParams() {
        switch (getScrollOrientation()) {
            case VERTICAL:
            default:
                return new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            case HORIZONTAL:
                return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (mContentView instanceof ViewGroup) {
            ((ViewGroup) mContentView).addView(child, index, params);
        } else {
            throw new UnsupportedOperationException("Can only add view to a ViewGroup");
        }
    }

    @Override
    public void setMode(LoadMode loadMode) {
        mLoadMode = loadMode;
    }

    @Override
    public LoadMode getMode() {
        return mLoadMode;
    }

    @Override
    public T getContentView() {
        return mContentView;
    }

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public void setLoading() {

    }

    @Override
    public void onLoadComplete() {
        if (isLoading()) {
            setState(State.RESET);
        }
    }

    @Override
    public void setOnRefreshListener() {

    }

    @Override
    public void onPull(State state, int distance) {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mLoadMode.isPullToLoad()) {
            return false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsIntercepted = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && mIsIntercepted) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (isReadyToPull()) {
                    mLastX = mInitialX = event.getX();
                    mLastY = mInitialY = event.getY();
                    mIsIntercepted = false;
                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (!isReadyToPull()) {
                    return false;
                }
                if (isLoading()) {
                    return true;
                }
                final float x = event.getX(), y = event.getY();
                final float scrollDirectionMove;
                final float otherDirectionMove;
                switch (getScrollOrientation()) {
                    case VERTICAL:
                    default:
                        scrollDirectionMove = y - mLastY;
                        otherDirectionMove = x - mLastX;
                        break;
                    case HORIZONTAL:
                        scrollDirectionMove = x - mLastX;
                        otherDirectionMove = y - mLastY;
                        break;

                }
                final float absMove = Math.abs(scrollDirectionMove);
                if (absMove > mTouchSlop && absMove > Math.abs(otherDirectionMove)) {
                    if (scrollDirectionMove >= 1f && isReadyToPullStart()) {
                        mLastX = x;
                        mLastY = y;
                        mIsIntercepted = true;
                        mCurLoadMode = LoadMode.PULL_FROM_START;
                    } else if (scrollDirectionMove <= -1f && isReadyToPullEnd()) {
                        mLastX = x;
                        mLastY = y;
                        mIsIntercepted = true;
                        mCurLoadMode = LoadMode.PULL_FROM_END;
                    }
                }
            }
            break;
        }

        return mIsIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mLoadMode.isPullToLoad()) {
            return false;
        }
        if (isLoading()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (isReadyToPull()) {
                    mLastX = mInitialX = event.getX();
                    mLastY = mInitialY = event.getY();
                    return true;
                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if(mIsIntercepted){
                    mLastX = event.getX();
                    mLastY = event.getY();
                    handlePull();
                    return true;
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mIsIntercepted) {
                    mIsIntercepted = false;
                    if (mState == State.RELEASE_TO_LOAD) {
                        setState(State.LOADING);
                        return true;
                    }

                    if (isLoading()) {
                        return true;
                    }
                    setState(State.RESET);
                }
            }
            break;
        }
        return false;
    }

    private boolean isReadyToPull() {
        switch (mLoadMode) {
            case PULL_FROM_START:
                return isReadyToPullStart();
            case PULL_FROM_END:
                return isReadyToPullEnd();
            case BOTH:
                return isReadyToPullStart() || isReadyToPullEnd();
            default:
                return false;
        }
    }

    private boolean isReadyToPullStart() {
        return isReadyToPull(Direction.START);
    }

    private boolean isReadyToPullEnd() {
        return isReadyToPull(Direction.END);
    }

    private boolean isReadyToPull(Direction direction) {
        switch (getScrollOrientation()) {
            case VERTICAL:
            default:
                return canScrollVertical(direction);
            case HORIZONTAL:
                return canScrollHorizontal(direction);
        }
    }

    private void setState(State state) {

    }

    private void handlePull(){

    }
}
