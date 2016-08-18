package com.droidworker.lib.constant;

/**
 * 状态枚举
 * @author https://github.com/DroidWorkerLYF
 */
public enum State {
    /**
     * 重置
     */
    RESET,
    /**
     * 向下(右)滑动
     */
    PULL_FROM_START,
    /**
     * 释放来加载更多
     */
    RELEASE_TO_LOAD,
    /**
     * 释放来更新
     */
    RELEASE_TO_UPDATE,
    /**
     * 加载中
     */
    LOADING,
    /**
     * 更新中
     */
    UPDATING,
    /**
     * 向上(左)滑动
     */
    PULL_FROM_END,
    /**
     * 自动更新
     */
    MANUAL_UPDATE,
    /**
     * 回弹
     */
    OVER_SCROLL
}
