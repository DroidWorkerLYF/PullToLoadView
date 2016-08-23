package com.droidworker.lib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.constant.Orientation;
import com.droidworker.lib.constant.State;

/**
 * BaseView,提供对于手势的处理,可以实现下拉加载更新,上拉加载更多,回弹,支持为指定Condition添加对应的视图,比如
 * 空白页,网络错误页.
 * 可以支持内容区域从Toolbar下方滚动({@link com.droidworker.lib.R.styleable#PullToLoadView_underBar}).
 * 支持的模式:{@link LoadMode}.根据手势,对应内部状态:{@link State}.
 * 可以指定{@link com.droidworker.lib.R.styleable#PullToLoadView_content_view_id}来生成ContentView,
 * 应对如果view的属性只通过xml可以配置的情况下或者通用的样式.
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class PullToLoadBaseView<T extends ViewGroup> extends FrameLayout
        implements IPullToLoad<T> {
    private static final String TAG = "PullToLoadBaseView";
    private static final float FRICTION = 2.0f;
    /**
     * 用于获取系统的actionbar size
     */
    private static final int[] THEME_ATTRS = { android.R.attr.actionBarSize };
    /**
     * Header view
     */
    private ILoadingLayout mHeader;
    /**
     * Footer view
     */
    private ILoadingLayout mFooter;
    /**
     * header的视图
     */
    private View mHeaderView;
    /**
     * footer的视图
     */
    private View mFooterView;
    /**
     * 实际显示内容的view
     */
    protected T mContentView;
    /**
     * Condition和view的映射
     */
    protected SparseArray<View> mConditionViews = new SparseArray<>(2);
    /**
     * 当前展示的Condition
     */
    protected View mCurConditionView;
    /**
     * 是否在Z轴上位于Toolbar或者自定义的导航栏下方.
     */
    private boolean mIsUnderBar;
    /**
     * 指定的内容view id
     */
    private int mContentViewId;
    /**
     * 支持的加载模式
     */
    private LoadMode mLoadMode = LoadMode.BOTH;
    /**
     * 当前出于的加载模式
     */
    private LoadMode mCurLoadMode;
    /**
     * 当前出于的状态
     */
    private State mState = State.RESET;
    /**
     * 垂直方向ActionBar高度,默认取系统的android.R.attr.actionBarSize
     * 水平方向,用户自定义的bar高度
     */
    private int mBarSize;
    /**
     * 是否intercept touch事件
     */
    private boolean mIsIntercepted;
    private int mTouchSlop;
    /**
     * 上一次触摸事件的坐标
     */
    private float mEndX, mEndY;
    /**
     * 触摸事件起始的坐标
     */
    private float mStartX, mStartY;
    /**
     * 加载状态回调
     */
    private PullToLoadListener mPullToLoadListener;
    /**
     * 平滑滚动动画
     */
    private ValueAnimator mValueAnimator;
    /**
     * 使用下拉回弹
     */
    private boolean mOverScrollStart;
    /**
     * 使用下拉回弹
     */
    private boolean mOverScrollEnd;
    /**
     * 全部加载.加载更多到最后一页,修改此属性为true,开启上拉回弹
     */
    private boolean mIsAllLoaded;
    /**
     * mode是否改变了
     */
    private boolean mModeChanged;
    /**
     * Top padding
     */
    private int mPaddingTop;
    /**
     * Left padding
     */
    private int mPaddingLeft;
    /**
     * Right padding
     */
    private int mPaddingRight;
    /**
     * Bottom padding
     */
    private int mPaddingBottom;

    public PullToLoadBaseView(Context context) {
        this(context, null);
    }

    public PullToLoadBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullToLoadView);
        mIsUnderBar = typedArray.getBoolean(R.styleable.PullToLoadView_underBar, false);
        mContentViewId = typedArray.getResourceId(R.styleable.PullToLoadView_content_view_id, 0);
        mBarSize = typedArray.getDimensionPixelSize(R.styleable.PullToLoadView_bar_size, 0);
        typedArray.recycle();
        if(getScrollOrientation() == Orientation.VERTICAL && mBarSize == 0){
            mBarSize = getActionBarSize();
        }

        //获取用户设定的padding,然后将容器的padding设置0,用户设定的padding将会设置到mContentView上,已保证
        //underBar模式正确
        mPaddingTop = super.getPaddingTop();
        mPaddingLeft = super.getPaddingLeft();
        mPaddingRight = super.getPaddingRight();
        mPaddingBottom = super.getPaddingBottom();
        super.setPadding(0, 0, 0, 0);

        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();

        initView();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            updateUI(mIsUnderBar);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mContentView = createContentView(mContentViewId);
        addContentView(mContentView);

        mHeader = createHeader();
        mHeaderView = mHeader.getLoadingView();
        addViewInternal(mHeaderView, 0, getLoadingLayoutLayoutParams());

        mFooter = createFooter();
        mFooterView = mFooter.getLoadingView();
        addViewInternal(mFooterView, getLoadingLayoutLayoutParams());

        // 将内容视图提到最前,主要是为了覆盖在header上.
        bringChildToFront(mContentView);
    }

    /**
     * 获取滚动方向, {@link Orientation#VERTICAL}或者{@link Orientation#HORIZONTAL},
     * 所有涉及方向的地方,都是用垂直方向作为default
     * @return 滚动方向
     */
    protected abstract Orientation getScrollOrientation();

    /**
     * 创建header
     * @return header
     */
    protected abstract ILoadingLayout createHeader();

    /**
     * 创建footer
     * @return footer
     */
    protected abstract ILoadingLayout createFooter();

    /**
     * 创建内容区域视图
     * @param layoutId 指定用来inflate视图的id
     * @return content view
     */
    protected abstract T createContentView(int layoutId);

    /**
     * 将contentView添加到父容器中
     * @param contentView 内容视图
     */
    private void addContentView(T contentView) {
        addViewInternal(contentView,
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * 因为Override了addView方法,所以提供此方法,用来在父容器中添加视图
     * @param child 要添加的视图
     * @param index 添加到的位置,-1表示添加到最后一项
     * @param params 布局参数
     */
    protected final void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    /**
     * {@link #addViewInternal(View, int, ViewGroup.LayoutParams)}
     */
    protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
        this.addViewInternal(child, -1, params);
    }

    /**
     * 获取用于header和footer的布局参数
     * @return 布局参数
     */
    private FrameLayout.LayoutParams getLoadingLayoutLayoutParams() {
        switch (getScrollOrientation()) {
        case VERTICAL:
        default:
            return new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
        case HORIZONTAL:
            return new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * 更新UI
     * @param isUnderBar {@link #mIsUnderBar}
     */
    private void updateUI(boolean isUnderBar) {
        if (isUnderBar) {
            mContentView.setClipToPadding(false);
        } else {
            mContentView.setClipToPadding(true);
        }
        final int headerSize = mHeader.getSize();
        switch (getScrollOrientation()) {
        case VERTICAL:
        default: {
            if (!mModeChanged) {
                if (isUnderBar) {
                    mContentView.setPadding(mPaddingLeft, mBarSize + mPaddingTop, mPaddingRight,
                            mPaddingBottom);
                    mHeaderView.setTranslationY(mBarSize - headerSize);
                } else {
                    mContentView.setPadding(mPaddingLeft, mContentView.getPaddingTop(), mPaddingRight, mPaddingBottom);
                    mHeaderView.setTranslationY(-headerSize);
                }
                ((LayoutParams) mFooterView.getLayoutParams()).gravity = Gravity.BOTTOM;
                mFooterView.setTranslationY(mFooter.getSize());
            }
        }
            break;
        case HORIZONTAL: {
            if (!mModeChanged) {
                if (isUnderBar) {
                    mContentView.setPadding(mBarSize + mPaddingLeft , mPaddingTop, mPaddingRight, mPaddingBottom);
                    mHeaderView.setTranslationX(mBarSize - headerSize);
                } else {
                    mContentView.setPadding(mContentView.getPaddingLeft(), mPaddingTop, mPaddingRight, mPaddingBottom);
                    mHeaderView.setTranslationX(-headerSize);
                }
                ((LayoutParams) mHeaderView.getLayoutParams()).gravity = Gravity.START;
                ((LayoutParams) mFooterView.getLayoutParams()).gravity = Gravity.END;
                mFooterView.setTranslationX(mFooter.getSize());
            }
        }
            break;
        }
        mContentView.setOverScrollMode(OVER_SCROLL_NEVER);
        updateContentUI(isUnderBar);
    }

    /**
     * @return ActionBar高度
     */
    private int getActionBarSize() {
        final TypedArray a = getContext().obtainStyledAttributes(THEME_ATTRS);
        try {
            return a.getDimensionPixelSize(0,
                    getResources().getDimensionPixelSize(R.dimen.actionBarSize));
        } finally {
            a.recycle();
        }
    }

    /**
     * 在UpdateUI时提供给子类的处理机会,针对当前加载模式进行调整
     * @param isUnderBar {@link #mIsUnderBar}
     */
    protected abstract void updateContentUI(boolean isUnderBar);

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        // 将child添加到内容视图中
        mContentView.addView(child, index, params);
    }

    @Override
    public void setMode(LoadMode loadMode) {
        if (mCurLoadMode == loadMode) {
            return;
        }
        mModeChanged = true;
        mLoadMode = loadMode;
        adjustForMode(mLoadMode);
        mModeChanged = false;
    }

    /**
     * 设置了LoadMode后进行一些相应的调整
     * @param loadMode 加载模式
     */
    private void adjustForMode(LoadMode loadMode) {
        switch (loadMode) {
        case PULL_FROM_START: {
            mOverScrollStart = false;
            mOverScrollEnd = true;
            mHeaderView.setVisibility(VISIBLE);
            mFooterView.setVisibility(INVISIBLE);
        }
            break;
        case MANUAL_ONLY: {
            mOverScrollStart = true;
            mOverScrollEnd = true;
            mHeaderView.setVisibility(INVISIBLE);
            mFooterView.setVisibility(INVISIBLE);
        }
            break;
        case PULL_FROM_END: {
            mOverScrollStart = true;
            mOverScrollEnd = false;
            mHeaderView.setVisibility(INVISIBLE);
            mFooterView.setVisibility(VISIBLE);
        }
            break;
        case PULL_FROM_START_AUTO_LOAD_MORE: {
            mOverScrollStart = false;
            mOverScrollEnd = true;
            mHeaderView.setVisibility(VISIBLE);
            mFooterView.setVisibility(INVISIBLE);
        }
            break;
        case DISABLED: {
            mOverScrollStart = true;
            mOverScrollEnd = true;
            mHeaderView.setVisibility(INVISIBLE);
            mFooterView.setVisibility(INVISIBLE);
        }
            break;
        default:
        case BOTH: {
            mOverScrollStart = false;
            mOverScrollEnd = false;
            mHeaderView.setVisibility(VISIBLE);
            mFooterView.setVisibility(VISIBLE);
        }
            break;
        }
        updateUI(mIsUnderBar);
    }

    @Override
    public LoadMode getMode() {
        return mLoadMode;
    }

    protected LoadMode getCurLoadMode() {
        return mCurLoadMode;
    }

    protected void setCurLoadMode(LoadMode loadMode) {
        mCurLoadMode = loadMode;
    }

    @Override
    public T getContentView() {
        return mContentView;
    }

    @Override
    public ILoadingLayout getHeader() {
        return mHeader;
    }

    @Override
    public ILoadingLayout getFooter() {
        return mFooter;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingTop = top;
        mPaddingLeft = left;
        mPaddingRight = right;
        mPaddingBottom = bottom;
        switch (getScrollOrientation()) {
        case VERTICAL:
        default: {
            mContentView.setPadding(mPaddingLeft, mPaddingTop + mBarSize, mPaddingRight, mPaddingBottom);
        }
            break;
        case HORIZONTAL: {
            mContentView.setPadding(mPaddingLeft + mBarSize, mPaddingTop, mPaddingRight, mPaddingBottom);
        }
            break;
        }
    }

    @Override
    public void addConditionView(View conditionView, int conditionType) {
        mConditionViews.put(conditionType, conditionView);
        addConditionView(conditionView);
    }

    private void addConditionView(View view){
        if(view == null){
            return;
        }
        view.setVisibility(GONE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if(mIsUnderBar){
            layoutParams.topMargin = mBarSize + mPaddingTop;
        } else {
            layoutParams.topMargin = mContentView.getPaddingTop();
        }
        addViewInternal(view, layoutParams);
    }

    @Override
    public void showConditionView(int conditionType) {
        View view = mConditionViews.get(conditionType, null);
        if(view == null){
            return;
        }
        if(mCurConditionView!=null){
            mCurConditionView.setVisibility(GONE);
        }
        mCurConditionView = view;
        view.setVisibility(VISIBLE);
    }

    @Override
    public boolean isLoading() {
        return mState == State.UPDATING || mState == State.LOADING || mState == State.MANUAL_UPDATE;
    }

    @Override
    public void setLoading() {
        if (!isLoading()) {
            setState(State.MANUAL_UPDATE);
        }
    }

    @Override
    public void onLoadComplete() {
        if (isLoading()) {
            setState(State.RESET);
        }
        if(mCurConditionView != null){
            mCurConditionView.setVisibility(GONE);
            mCurConditionView = null;
        }
    }

    @Override
    public void onAllLoaded() {
        mIsAllLoaded = true;
        mOverScrollEnd = true;
        mFooterView.setVisibility(INVISIBLE);
    }

    protected boolean isAllLoaded() {
        return mIsAllLoaded;
    }

    @Override
    public void setOnPullToLoadListener(PullToLoadListener pullToLoadListener) {
        mPullToLoadListener = pullToLoadListener;
    }

    protected PullToLoadListener getPullToLoadListener() {
        return mPullToLoadListener;
    }

    @Override
    public void onPull(State state, float distance) {
        if (mCurLoadMode == null) {
            return;
        }
        switch (mCurLoadMode) {
        case PULL_FROM_START: {
            mHeader.onPull(state, distance);
        }
            break;
        case PULL_FROM_END: {
            mFooter.onPull(state, distance);
        }
            break;
        }
    }

    public int getBarHeight() {
        return mBarSize;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mLoadMode.isPullToLoad() && !isOverScroll()) {
            Log.i(TAG, "intercept pull to load not enable");
            return false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            Log.i(TAG, "intercept action cancel || up");
            mIsIntercepted = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && mIsIntercepted) {
            Log.i(TAG, "intercept action is not down and is intercepted");
            return true;
        }

        switch (action) {
        case MotionEvent.ACTION_DOWN: {
            if (isReadyToPull() || isOverScroll()) {
                Log.i(TAG, "intercept action down");
                mEndX = mStartX = event.getX();
                mEndY = mStartY = event.getY();
                mIsIntercepted = false;
            }
        }
            break;
        case MotionEvent.ACTION_MOVE: {
            if (!isReadyToPull() && !isOverScroll()) {
                return false;
            }
            if (isLoading()) {
                Log.i(TAG, "intercept action move is loading");
                return true;
            }
            final float x = event.getX(), y = event.getY();
            final float scrollDirectionMove;
            final float otherDirectionMove;
            switch (getScrollOrientation()) {
            case VERTICAL:
            default:
                scrollDirectionMove = y - mEndY;
                otherDirectionMove = x - mEndX;
                break;
            case HORIZONTAL:
                scrollDirectionMove = x - mEndX;
                otherDirectionMove = y - mEndY;
                break;

            }
            final float absMove = Math.abs(scrollDirectionMove);
            if (absMove > mTouchSlop && absMove > Math.abs(otherDirectionMove)) {
                Log.i(TAG, scrollDirectionMove + "");
                if (scrollDirectionMove >= 1f && isReadyToPullStart()) {
                    mEndX = x;
                    mEndY = y;
                    mIsIntercepted = true;
                    mCurLoadMode = LoadMode.PULL_FROM_START;
                    setState(State.PULL_FROM_START);
                    Log.i(TAG, "intercept action move pull from start");
                } else if (scrollDirectionMove <= -1f && isReadyToPullEnd()) {
                    mEndX = x;
                    mEndY = y;
                    mIsIntercepted = true;
                    mCurLoadMode = LoadMode.PULL_FROM_END;
                    setState(State.PULL_FROM_END);
                    Log.i(TAG, "intercept action move pull from end");
                }
            }
        }
            break;
        }

        return mIsIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mLoadMode.isPullToLoad() && !isOverScroll()) {
            Log.i(TAG, "touch action pull to load not enable");
            return false;
        }
        if (isLoading()) {
            Log.i(TAG, "touch action is loading");
            return true;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            if (isReadyToPull() || isOverScroll()) {
                Log.i(TAG, "touch action action down");
                mEndX = mStartX = event.getX();
                mEndY = mStartY = event.getY();
                return true;
            }
        }
            break;
        case MotionEvent.ACTION_MOVE: {
            if (mIsIntercepted) {
                Log.i(TAG, "touch action action move");
                mEndX = event.getX();
                mEndY = event.getY();
                handlePull();
                return true;
            }
        }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL: {
            if (mIsIntercepted) {
                mIsIntercepted = false;
                if (mOverScrollStart && mCurLoadMode == LoadMode.PULL_FROM_START
                        || mOverScrollEnd && mCurLoadMode == LoadMode.PULL_FROM_END) {
                    Log.i(TAG, "touch action up | cancel over scroll");
                    setState(State.OVER_SCROLL);
                    return true;
                }

                if (mState == State.RELEASE_TO_UPDATE) {
                    Log.i(TAG, "touch up | cancel start updating");
                    setState(State.UPDATING);
                    return true;
                } else if (mState == State.RELEASE_TO_LOAD) {
                    Log.i(TAG, "touch up | cancel start loading");
                    setState(State.LOADING);
                    return true;
                }

                if (isLoading()) {
                    Log.i(TAG, "touch action up | cancel is loading");
                    return true;
                }

                Log.i(TAG, "touch action up | cancel reset");
                setState(State.RESET);
            }
        }
            break;
        }
        return false;
    }

    /**
     * 是否要进行回弹
     * @return true则进行回弹
     */
    private boolean isOverScroll() {
        switch (mLoadMode) {
        case PULL_FROM_START:
            return isReadyToPullEnd() && mOverScrollEnd;
        case PULL_FROM_START_AUTO_LOAD_MORE:
            return isReadyToPullEnd() && mOverScrollEnd;
        case PULL_FROM_END:
            return isReadyToPullStart() && mOverScrollStart;
        case MANUAL_ONLY:
        case DISABLED:
            return (isReadyToPullStart() && mOverScrollStart)
                    || (isReadyToPullEnd() && mOverScrollEnd);
        default:
            return false;
        }
    }

    /**
     * 是否可以拉动
     * @return true 可以拉动
     */
    private boolean isReadyToPull() {
        switch (mLoadMode) {
        case PULL_FROM_START:
            return isReadyToPullStart();
        case PULL_FROM_END:
            return isReadyToPullEnd();
        case BOTH:
            return isReadyToPullStart() || isReadyToPullEnd();
        case PULL_FROM_START_AUTO_LOAD_MORE:
            return isReadyToPullStart();
        default:
            return false;
        }
    }

    /**
     * 垂直方向:向下拉动
     * 水平方向:向右拉动
     * @return true 可以拉动
     */
    private boolean isReadyToPullStart() {
        Log.i(TAG, isReadyToPull(Direction.START) + "  PULL_FROM_START");
        return isReadyToPull(Direction.START);
    }

    /**
     * 垂直方向:向上拉动
     * 水平方向:向左拉动
     * @return true 可以拉动
     */
    private boolean isReadyToPullEnd() {
        Log.i(TAG, isReadyToPull(Direction.END) + "  isReadyToPullEnd");
        return isReadyToPull(Direction.END);
    }

    /**
     * 判断在指定的方向上,view是否还可以继续滑动
     * @param direction 指定的方向
     * @return true则可以拉到
     */
    private boolean isReadyToPull(Direction direction) {
        switch (getScrollOrientation()) {
        case VERTICAL:
        default:
            if (mCurConditionView != null) {
                switch (direction) {
                case START:
                    return true;
                case END:
                    return false;
                }
            }
            return !canScrollVertical(direction);
        case HORIZONTAL:
            return !canScrollHorizontal(direction);
        }
    }

    /**
     * 设置状态
     * @param state 状态
     */
    protected void setState(State state) {
        if(mState == state){
            return;
        }
        mState = state;
        switch (mState) {
        case PULL_FROM_START:
            if (!mOverScrollStart) {
                mHeader.show();
                onPull(state, 0);
            }
            break;
        case PULL_FROM_END:
            if (!mOverScrollEnd) {
                mFooter.show();
                onPull(state, 0);
            }
            break;
        case LOADING:
        case UPDATING:
            onPull(state, 0);
            onLoading();
            break;
        case RELEASE_TO_LOAD:
        case RELEASE_TO_UPDATE:
            onPull(state, 0);
            break;
        case MANUAL_UPDATE:
            mHeader.show();
            onPull(state, 0);
            manualLoad();
            break;
        case OVER_SCROLL:
            reset();
            break;
        case RESET:
            reset();
            break;
        }
    }

    /**
     * 进入loading状态
     */
    protected void onLoading() {
        switch (mCurLoadMode) {
        case PULL_FROM_START:
        default:
            mIsAllLoaded = false;
            smoothScrollTo(-mBarSize - (mFooter.getSize() - mBarSize));
            if (mPullToLoadListener != null) {
                mPullToLoadListener.onLoadNew();
            }
            break;
        case PULL_FROM_END:
            smoothScrollTo(mFooter.getSize());
            if (mPullToLoadListener != null) {
                mPullToLoadListener.onLoadMore();
            }
            break;
        }
    }

    protected void manualLoad() {
        mCurLoadMode = LoadMode.PULL_FROM_START;
        onLoading();
    }

    /**
     * 重置状态
     */
    protected void reset() {
        mCurLoadMode = null;
        mIsIntercepted = false;
        mEndX = mStartX = 0;
        mEndY = mStartY = 0;
        mHeader.hide();
        smoothScrollTo(0);
    }

    protected State getState() {
        return mState;
    }

    /**
     * 处理touch事件产生的拖动
     */
    private void handlePull() {
        final float startValue;
        final float endValue;

        switch (getScrollOrientation()) {
        case VERTICAL:
        default: {
            startValue = mStartY;
            endValue = mEndY;
        }
            break;
        case HORIZONTAL: {
            startValue = mStartX;
            endValue = mEndX;
        }
            break;
        }
        final float scrollValue;
        final int size;
        switch (mCurLoadMode) {
        case PULL_FROM_START:
        default:
            scrollValue = Math.round(Math.min(startValue - endValue, 0) / FRICTION);
            size = mHeader.getSize();
            break;
        case PULL_FROM_END:
            scrollValue = Math.round(Math.max(startValue - endValue, 0) / FRICTION);
            size = mFooter.getSize();
            break;
        }

        if (scrollValue != 0 && !isLoading()) {
            scroll(scrollValue);
            switch (mCurLoadMode) {
            case PULL_FROM_START:
            default: {
                if (Math.abs(scrollValue) > size) {
                    setState(State.RELEASE_TO_UPDATE);
                } else {
                    setState(State.PULL_FROM_START);
                }
            }
                break;
            case PULL_FROM_END: {
                if (Math.abs(scrollValue) > size) {
                    setState(State.RELEASE_TO_LOAD);
                } else {
                    setState(State.PULL_FROM_END);
                }
            }
                break;
            }
            onPull(mState, scrollValue);
        }
    }

    private void scroll(float scrollValue) {
        switch (getScrollOrientation()) {
        case VERTICAL:
        default:
            scrollTo(0, (int) scrollValue);
            break;
        case HORIZONTAL:
            scrollTo((int) scrollValue, 0);
            break;
        }
    }

    /**
     * 平滑滚动
     * @param scrollValue 滚动数值
     */
    private void smoothScrollTo(float scrollValue) {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        final int oldScrollValue;
        switch (getScrollOrientation()) {
        case HORIZONTAL:
            oldScrollValue = getScrollX();
            break;
        case VERTICAL:
        default:
            oldScrollValue = getScrollY();
            break;
        }
        if (oldScrollValue == scrollValue) {
            return;
        }
        mValueAnimator = ValueAnimator.ofInt(oldScrollValue, (int) scrollValue);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scroll((int) animation.getAnimatedValue());
            }
        });
        mValueAnimator.start();
    }
}
