package com.droidworker.lib;

import com.droidworker.lib.constant.Direction;
import com.droidworker.lib.constant.LoadMode;
import com.droidworker.lib.constant.State;

import android.view.View;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public interface IPullToLoad<T extends View> {

    /**
     * 设置加载模式
     * @param loadMode 加载模式
     */
    void setMode(LoadMode loadMode);

    /**
     * @return 加载模式
     */
    LoadMode getMode();

    /**
     * @return 当前内容视图
     */
    T getContentView();

    /**
     * @return header
     */
    ILoadingLayout getHeader();

    /**
     * @return footer
     */
    ILoadingLayout getFooter();

    /**
     * 因为效果的实现受到padding的影响,所以要提供此方法实现正确的行为
     */
    void setPadding(int left, int top, int right, int bottom);

    /**
     * 竖直方向上是否还可以沿指定方向滚动
     * @param direction 方向
     * @return true则可以继续滚动
     */
    boolean canScrollVertical(Direction direction);

    /**
     * 水平方向上是否还可以沿指定方向滚动
     * @param direction 方向
     * @return true则可以继续滚动
     */
    boolean canScrollHorizontal(Direction direction);

    /**
     * 是否正在加载
     * @return true 正在加载
     */
    boolean isLoading();

    /**
     * 设置为加载({@link State#MANUAL_UPDATE})
     */
    void setLoading();

    /**
     * 设置加载完成
     */
    void onLoadComplete();

    /**
     * 当所有内容加载完毕后,调用此方法,开启回弹
     */
    void onAllLoaded();

    /**
     * 设置加载回调
     * @param pullToLoadListener 回调
     */
    void setOnPullToLoadListener(PullToLoadListener pullToLoadListener);

    /**
     * 处理状态和滚动距离
     * @param state 状态
     * @param distance 距离
     */
    void onPull(State state, float distance);
}
