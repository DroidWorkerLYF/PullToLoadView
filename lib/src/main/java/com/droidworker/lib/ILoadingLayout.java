package com.droidworker.lib;

import com.droidworker.lib.constant.State;

import android.view.View;

/**
 * 自定义的header或者footer需要实现此接口来让{@link PullToLoadBaseView}调用
 * @author https://github.com/DroidWorkerLYF
 */
public interface ILoadingLayout {

    /**
     * 获取大小(高度/宽度)
     * @return
     */
    int getSize();

    /**
     * 处理状态和滑动距离
     * @param state 当前状态
     * @param distance 滑动距离
     */
    void onPull(State state, float distance);

    /**
     * 显示
     */
    void show();

    /**
     * 隐藏
     */
    void hide();

    /**
     * 获取实现了此接口的view本身
     * @return view
     */
    View getLoadingView();
}
