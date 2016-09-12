package com.droidworker.pulltoloadview;

import com.droidworker.pulltoloadview.constant.Direction;
import com.droidworker.pulltoloadview.constant.LoadMode;
import com.droidworker.pulltoloadview.constant.Orientation;
import com.droidworker.pulltoloadview.constant.State;
import com.droidworker.pulltoloadview.impl.LoadingLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * BaseView,提供对于手势的处理,可以实现下拉加载更新,上拉加载更多,回弹,支持为指定Condition添加对应的视图,比如
 * 空白页,网络错误页.
 * 可以支持内容区域从Toolbar下方滚动({@link R.styleable#PullToLoadView_underBar}).
 * 支持的模式:{@link LoadMode}.根据手势,对应内部状态:{@link State}.
 * 可以指定{@link R.styleable#PullToLoadView_content_view_id}来生成ContentView,
 * 应对如果view的属性只通过xml可以配置的情况下或者通用的样式.
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class PullToLoadBaseView<T extends ViewGroup> extends FrameLayout
        implements IPullToLoad<T>, NestedScrollingParent {
    private static final String TAG = "PullToLoadBaseView";
    private static final boolean DEBUG = true;
    private static final int DEFAULT_ANIM_DURATION = 300;
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
     * 自定义内容的layout id
     */
    private int mContentLayoutId;
    /**
     * 自定义header的layout id
     */
    private int mHeaderLayoutId;
    /**
     * 自定义footer的layout id
     */
    private int mFooterLayoutId;
    /**
     * State为reset时,滑动到top动画时间
     */
    private int mScrollTopDuration;
    /**
     * State为reset时,滑动到bottom动画时间
     */
    private int mScrollBottomDuration;
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
     * 是否支持NestedScroll
     */
    private boolean isNestedScrollEnable;
    /**
     * NestedScroll下当前的滚动距离
     */
    private int mNestedScrollOffset;
    /**
     * NestedScroll时滚动偏移量
     * [0]表示滚动方向上的偏移量
     * [1]表示非滚动方向上的偏移量
     */
    private int[] mDirectionMove = new int[2];
    /**
     * true则表示走NestedScroll的逻辑
     */
    private boolean mHandleByNestedScroll;
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
    /**
     * 使用bringChildToFront,将header置于content之上,但是没有背景色会导致header和内容区域
     * 看起来重叠在了一起,所以underBar模式下需要给出背景的resId,如果自定义header设置了背景,
     * 则可忽略此项
     */
    private int mHeaderBgResId;
    /**
     * true代表完成了加载更新或者加载更多,配合reset更新使用
     */
    private boolean mDone;

    public PullToLoadBaseView(Context context) {
        this(context, null);
    }

    public PullToLoadBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullToLoadView);
        mIsUnderBar = typedArray.getBoolean(R.styleable.PullToLoadView_underBar, false);
        mContentLayoutId = typedArray.getResourceId(R.styleable.PullToLoadView_content_view_id, 0);
        mHeaderLayoutId = typedArray.getResourceId(R.styleable.PullToLoadView_header_view_id, 0);
        mFooterLayoutId = typedArray.getResourceId(R.styleable.PullToLoadView_footer_view_id, 0);
        mBarSize = typedArray.getDimensionPixelSize(R.styleable.PullToLoadView_bar_size, 0);
        mHeaderBgResId = typedArray
                .getResourceId(R.styleable.PullToLoadView_underbar_header_background, 0);
        mScrollTopDuration = typedArray.getInt(R.styleable.PullToLoadView_scroll_to_top_duration,
                DEFAULT_ANIM_DURATION);
        mScrollBottomDuration = typedArray.getInt(
                R.styleable.PullToLoadView_scroll_to_bottom_duration, DEFAULT_ANIM_DURATION);
        typedArray.recycle();
        if (getScrollOrientation() == Orientation.VERTICAL && mBarSize == 0 && mIsUnderBar) {
            mBarSize = getActionBarSize();
        }

        // 获取用户设定的padding,然后将容器的padding设置0,用户设定的padding将会设置到mContentView上,已保证
        // underBar模式正确
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
        mContentView = createContentView(mContentLayoutId);
        isNestedScrollEnable = ViewCompat.isNestedScrollingEnabled(mContentView);
        addContentView(mContentView);
        mContentView.setOverScrollMode(OVER_SCROLL_NEVER);

        mHeader = createHeader();
        mHeaderView = mHeader.getLoadingView();
        addViewInternal(mHeaderView, 0, getLoadingLayoutLayoutParams());

        mFooter = createFooter();
        mFooterView = mFooter.getLoadingView();
        addViewInternal(mFooterView, getLoadingLayoutLayoutParams());

        bringChildToFront(mHeaderView);
        if (mHeaderBgResId != 0) {
            mHeaderView.setBackgroundResource(mHeaderBgResId);
        }
    }

    /**
     * 获取滚动方向, {@link Orientation#VERTICAL}或者{@link Orientation#HORIZONTAL},
     * 所有涉及方向的地方,都是用垂直方向作为default
     * @return 滚动方向
     */
    protected abstract Orientation getScrollOrientation();

    /**
     * 根据滚动方向获取滚动偏移量
     * @return 偏移量
     */
    private int getInternalScrollOffset() {
        switch (getScrollOrientation()) {
        case VERTICAL:
        default:
            return getScrollY();
        case HORIZONTAL:
            return getScrollX();
        }
    }

    /**
     * 创建header
     * @return header
     */
    protected ILoadingLayout createHeader() {
        if (mHeaderLayoutId != 0) {
            View view = LayoutInflater.from(getContext()).inflate(mHeaderLayoutId, this, false);
            if (view instanceof ILoadingLayout) {
                return (ILoadingLayout) view;
            } else {
                throw new UnsupportedOperationException(
                        "a custom header layout must implements ILoadingLayout");
            }
        }
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

    /**
     * 创建footer
     * @return footer
     */
    protected ILoadingLayout createFooter() {
        if (mFooterLayoutId != 0) {
            View view = LayoutInflater.from(getContext()).inflate(mFooterLayoutId, this, false);
            if (view instanceof ILoadingLayout) {
                return (ILoadingLayout) view;
            } else {
                throw new UnsupportedOperationException(
                        "a custom footer layout must implements ILoadingLayout");
            }
        }
        return new LoadingLayout(getContext(), getScrollOrientation());
    }

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
        if (!mModeChanged) {
            final int headerSize = mHeader.getSize();
            switch (getScrollOrientation()) {
            case VERTICAL:
            default:
                if (isUnderBar) {
                    mContentView.setPadding(mPaddingLeft, mBarSize + mPaddingTop, mPaddingRight,
                            mPaddingBottom);
                    mHeaderView.setTranslationY(mBarSize - headerSize);
                } else {
                    mContentView.setPadding(mPaddingLeft, mContentView.getPaddingTop(),
                            mPaddingRight, mPaddingBottom);
                    mHeaderView.setTranslationY(-headerSize);
                }
                ((LayoutParams) mFooterView.getLayoutParams()).gravity = Gravity.BOTTOM;
                mFooterView.setTranslationY(mFooter.getSize());
                break;
            case HORIZONTAL:
                if (isUnderBar) {
                    mContentView.setPadding(mBarSize + mPaddingLeft, mPaddingTop, mPaddingRight,
                            mPaddingBottom);
                    mHeaderView.setTranslationX(mBarSize - headerSize);
                } else {
                    mContentView.setPadding(mContentView.getPaddingLeft(), mPaddingTop,
                            mPaddingRight, mPaddingBottom);
                    mHeaderView.setTranslationX(-headerSize);
                }
                ((LayoutParams) mHeaderView.getLayoutParams()).gravity = Gravity.START;
                ((LayoutParams) mFooterView.getLayoutParams()).gravity = Gravity.END;
                mFooterView.setTranslationX(mFooter.getSize());
                break;
            }
        }
        mHeader.hide();
        mFooter.hide();
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
        mOverScrollStart = loadMode.canOverScrollStart();
        mOverScrollEnd = loadMode.canOverScrollEnd();
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
        default:
            mContentView.setPadding(mPaddingLeft, mPaddingTop + mBarSize, mPaddingRight,
                    mPaddingBottom);
            break;
        case HORIZONTAL:
            mContentView.setPadding(mPaddingLeft + mBarSize, mPaddingTop, mPaddingRight,
                    mPaddingBottom);
            break;
        }
    }

    @Override
    public void addConditionView(View conditionView, int conditionType) {
        mConditionViews.put(conditionType, conditionView);
        addConditionView(conditionView);
    }

    private void addConditionView(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(GONE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (mIsUnderBar) {
            layoutParams.topMargin = mBarSize + mPaddingTop;
        } else {
            layoutParams.topMargin = mContentView.getPaddingTop();
        }
        addViewInternal(view, layoutParams);
    }

    @Override
    public void showConditionView(int conditionType) {
        View view = mConditionViews.get(conditionType, null);
        if (view == null) {
            return;
        }
        if (mCurConditionView != null) {
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
            mDone = true;
            setState(State.RESET);
        }
        if (mCurConditionView != null) {
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

    @Override
    public boolean isAllLoaded() {
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
        case PULL_FROM_START:
            mHeader.onPull(state, distance);
            break;
        case PULL_FROM_END:
            mFooter.onPull(state, distance);
            break;
        }
    }

    public int getBarHeight() {
        return mBarSize;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mLoadMode.isPullToLoad() && !isOverScroll()) {
            Log("intercept pull to load not enable");
            return false;
        }
        // 如果已经设置了滑动状态并且视图时支持NestedScroll的,则更新touch事件的位置,不拦截事件
        if (mCurLoadMode != null && isNestedScrollEnable) {
            mHandleByNestedScroll = true;
            mEndX = event.getX();
            mEndY = event.getY();
            return false;
        }
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            Log("intercept action cancel || up");
            mIsIntercepted = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && mIsIntercepted) {
            Log("intercept action is not down and is intercepted");
            return true;
        }

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (isReadyToPull() || isOverScroll()) {
                Log("intercept action down");
                mEndX = mStartX = event.getX();
                mEndY = mStartY = event.getY();
                mIsIntercepted = false;
                break;
            }
        case MotionEvent.ACTION_MOVE:
            if (!isReadyToPull() && !isOverScroll()) {
                return false;
            }
            if (isLoading()) {
                Log("intercept action move is loading");
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
            // 如果是支持NestedScroll的视图类型,则不拦截touch时间,只设置状态
            if (absMove > mTouchSlop && absMove > Math.abs(otherDirectionMove)) {
                if (scrollDirectionMove >= 1f && isReadyToPullStart()) {
                    mEndX = x;
                    mEndY = y;
                    mIsIntercepted = !isNestedScrollEnable;
                    mCurLoadMode = LoadMode.PULL_FROM_START;
                    setState(State.PULL_FROM_START);
                    Log("intercept action move pull from start");
                } else if (scrollDirectionMove <= -1f && isReadyToPullEnd()) {
                    mEndX = x;
                    mEndY = y;
                    mIsIntercepted = !isNestedScrollEnable;
                    mCurLoadMode = LoadMode.PULL_FROM_END;
                    setState(State.PULL_FROM_END);
                    Log("intercept action move pull from end");
                }
            }
            break;
        }

        return mIsIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mLoadMode.isPullToLoad() && !isOverScroll()) {
            Log("touch action pull to load not enable");
            return false;
        }
        if (isLoading()) {
            Log("touch action is loading");
            return true;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (isReadyToPull() || isOverScroll()) {
                mEndX = mStartX = event.getX();
                mEndY = mStartY = event.getY();
                return true;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (mIsIntercepted) {
                mEndX = event.getX();
                mEndY = event.getY();
                handlePull();
                return true;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (mIsIntercepted) {
                mIsIntercepted = false;
                return onActionUpOrCancel();
            }
            break;
        }
        return false;
    }

    /**
     * Action为up或者cancel时的处理方法,当NestedScroll结束时,需要同样的操作,所以将这部分代码提供为独立方法
     */
    private boolean onActionUpOrCancel() {
        if (mOverScrollStart && mCurLoadMode == LoadMode.PULL_FROM_START
                || mOverScrollEnd && mCurLoadMode == LoadMode.PULL_FROM_END) {
            Log("touch action up | cancel over scroll");
            setState(State.OVER_SCROLL);
            return true;
        }

        if (mState == State.RELEASE_TO_UPDATE) {
            Log("touch up | cancel start updating");
            setState(State.UPDATING);
            return true;
        } else if (mState == State.RELEASE_TO_LOAD) {
            Log("touch up | cancel start loading");
            setState(State.LOADING);
            return true;
        }

        if (isLoading()) {
            Log("touch action up | cancel is loading");
            return true;
        }

        Log("touch action up | cancel reset");
        setState(State.RESET);
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
        return isReadyToPull(Direction.START);
    }

    /**
     * 垂直方向:向上拉动
     * 水平方向:向左拉动
     * @return true 可以拉动
     */
    private boolean isReadyToPullEnd() {
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
        if (mState == state) {
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
            mCurLoadMode = LoadMode.PULL_FROM_START;
            mHeader.show();
            manualLoad();
            onPull(state, -mHeader.getSize());
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
            smoothScrollTo(-mHeader.getSize());
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
        onPull(State.RESET, mDone
                ? mCurLoadMode == LoadMode.PULL_FROM_START ? mHeader.getSize() : -mFooter.getSize()
                : 0);
        smoothScrollTo(0);
        mDone = false;
        mCurLoadMode = null;
        mIsIntercepted = false;
        mEndX = mStartX = 0;
        mEndY = mStartY = 0;
        mState = State.RESET;
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
        default:
            startValue = mStartY;
            endValue = mEndY;
            break;
        case HORIZONTAL:
            startValue = mStartX;
            endValue = mEndX;
            break;
        }
        final float scrollValue;
        switch (mCurLoadMode) {
        case PULL_FROM_START:
        default:
            scrollValue = Math.round(Math.min(startValue - endValue, 0) / FRICTION);
            break;
        case PULL_FROM_END:
            scrollValue = Math.round(Math.max(startValue - endValue, 0) / FRICTION);
            break;
        }

        if (scrollValue != 0 && !isLoading()) {
            scroll(scrollValue);
            updateStateWhenPull(scrollValue);
            onPull(mState, scrollValue);
        }
    }

    /**
     * 根据状态获取header或者footer的size
     * @return size
     */
    private int getLoadingLayoutSize() {
        switch (mCurLoadMode) {
        case PULL_FROM_START:
        default:
            return mHeader.getSize();
        case PULL_FROM_END:
            return mFooter.getSize();
        }
    }

    /**
     * 处理拖动时的状态变化
     * @param scrollValue 偏移量
     */
    private void updateStateWhenPull(float scrollValue) {
        final int size = getLoadingLayoutSize();
        switch (mCurLoadMode) {
        case PULL_FROM_START:
        default:
            if (Math.abs(scrollValue) > size) {
                setState(State.RELEASE_TO_UPDATE);
            } else {
                setState(State.PULL_FROM_START);
            }
            break;
        case PULL_FROM_END:
            if (Math.abs(scrollValue) > size) {
                setState(State.RELEASE_TO_LOAD);
            } else {
                setState(State.PULL_FROM_END);
            }
            break;
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
        if (scrollValue == 0 && mDone) {
            mValueAnimator.setDuration(mCurLoadMode == LoadMode.PULL_FROM_START ? mScrollTopDuration
                    : mScrollBottomDuration);
        } else {
            mValueAnimator.setDuration(DEFAULT_ANIM_DURATION);
        }
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scroll((int) animation.getAnimatedValue());
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mState == State.RESET) {
                    mHeader.hide();
                }
            }
        });
        mValueAnimator.start();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        switch (getScrollOrientation()) {
        case VERTICAL:
        default:
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
        case HORIZONTAL:
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL;
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        onStopNestedScroll();
    }

    /**
     * 停止NestedScroll
     */
    private void onStopNestedScroll() {
        onActionUpOrCancel();
        mNestedScrollOffset = 0;
        mHandleByNestedScroll = false;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
            int dyUnconsumed) {
        // do nothing,因为全部偏移量都会被父容器吃掉,此方法不会触发
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (mHandleByNestedScroll) {
            final float endValue,startValue;
            switch (getScrollOrientation()) {
                case VERTICAL:
                default:
                    startValue = mStartY;
                    endValue = mEndY;
                    break;
                case HORIZONTAL:
                    startValue = mStartX;
                    endValue = mEndX;
                    break;
            }
            switch (mCurLoadMode) {
            case PULL_FROM_START:
                if (endValue > startValue) {
                    consumed[0] = dx;
                    consumed[1] = dy;
                    handlePull();
                } else {
                    mHandleByNestedScroll = false;
                    mHeader.hide();
                }
                break;
            case PULL_FROM_END:
                if (endValue < startValue) {
                    consumed[0] = dx;
                    consumed[1] = dy;
                    handlePull();
                } else {
                    mHandleByNestedScroll = false;
                }
                break;
            }
        } else {
            int offset;
            switch (getScrollOrientation()) {
            case VERTICAL:
            default:
                offset = dy;
                break;
            case HORIZONTAL:
                offset = dx;
                break;
            }
            getDirectionOffset(dx, dy);
            if (offset < 0 && isReadyToPullStart()) {
                consumed[0] = dx;
                consumed[1] = dy;

                if (mCurLoadMode == null) {
                    final float absMove = Math.abs(mDirectionMove[0]);
                    if (absMove > Math.abs(mDirectionMove[1]) && mDirectionMove[0] <= -1f) {
                        mCurLoadMode = LoadMode.PULL_FROM_START;
                        setState(State.PULL_FROM_START);
                        Log("onNestedScroll pull from start");
                        handleNestedScrollPull(mDirectionMove[0]);
                    }
                } else {
                    if(mHeaderView.getVisibility() != View.VISIBLE && mLoadMode.shouldShowHeader() ){
                        mHeader.show();
                    }
                    handleNestedScrollPull(mDirectionMove[0]);
                }
            } else if (offset > 0 && isReadyToPullEnd()) {
                consumed[0] = dx;
                consumed[1] = dy;

                if (mCurLoadMode == null) {
                    final float absMove = Math.abs(mDirectionMove[0]);
                    if (absMove > Math.abs(mDirectionMove[1]) && mDirectionMove[0] >= 1f
                            && isReadyToPullEnd()) {
                        mCurLoadMode = LoadMode.PULL_FROM_END;
                        setState(State.PULL_FROM_END);
                        Log("onNestedScroll pull from end");
                        handleNestedScrollPull(mDirectionMove[0]);
                    }
                } else {
                    handleNestedScrollPull(mDirectionMove[0]);
                }
            } else if (mCurLoadMode != null) {
                switch (mCurLoadMode) {
                case PULL_FROM_START:
                    if (getInternalScrollOffset() < 0) {
                        consumed[0] = dx;
                        consumed[1] = dy;
                        handleNestedScrollPull(mDirectionMove[0]);
                    } else {
                        if(mHeaderView.getVisibility() != View.INVISIBLE){
                            mHeader.hide();
                        }
                    }
                    break;
                case PULL_FROM_END:
                    if (getInternalScrollOffset() > 0) {
                        consumed[0] = dx;
                        consumed[1] = dy;
                        handleNestedScrollPull(mDirectionMove[0]);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 根据滚动方向获取对应的偏移量
     * @return [0]表示滚动方向上的偏移量
     *         [1]表示非滚动方向上的偏移量
     */
    private int[] getDirectionOffset(int dx, int dy) {
        switch (getScrollOrientation()) {
        case VERTICAL:
        default:
            mDirectionMove[0] = dy;
            mDirectionMove[1] = dx;
            break;
        case HORIZONTAL:
            mDirectionMove[0] = dx;
            mDirectionMove[1] = dy;
            break;
        }
        return mDirectionMove;
    }

    /**
     * 处理NestedScroll下的滚动
     * @param scrollValue 偏移量
     */
    private void handleNestedScrollPull(float scrollValue) {
        mNestedScrollOffset += Math.round(scrollValue) / FRICTION;
        if (mNestedScrollOffset != 0 && !isLoading()) {
            scroll(mNestedScrollOffset);
            updateStateWhenPull(mNestedScrollOffset);
            onPull(mState, mNestedScrollOffset);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return super.getNestedScrollAxes();
    }

    private void Log(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }
}
