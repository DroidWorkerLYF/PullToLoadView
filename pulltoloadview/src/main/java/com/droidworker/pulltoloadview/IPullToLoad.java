package com.droidworker.pulltoloadview;

import com.droidworker.pulltoloadview.constant.Direction;
import com.droidworker.pulltoloadview.constant.LoadMode;
import com.droidworker.pulltoloadview.constant.State;

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
     * 增加特殊情况的视图,例如空数据,错误页面
     * @param conditionView 特殊视图
     * @param conditionType 情况
     */
    void addConditionView(View conditionView, int conditionType);

    /**
     * 展示指定情况下的视图
     * @param conditionType 指定情况
     */
    void showConditionView(int conditionType);

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
     * 是否全部加载完毕
     * @return true 则表示调用过onAllLoaded,已经都加载完了
     */
    boolean isAllLoaded();

    /**
     * 设置是否全部加载完成,
     * 当所有内容加载完毕后,调用此方法,开启回弹,当下拉加载更新后,状态会还原
     */
    void setAllLoaded(boolean isAllLoaded);

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
