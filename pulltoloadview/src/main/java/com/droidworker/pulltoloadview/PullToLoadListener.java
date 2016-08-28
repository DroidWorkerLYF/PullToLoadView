package com.droidworker.pulltoloadview;

/**
 * 拉动触发的回调
 * @author https://github.com/DroidWorkerLYF
 */
public interface PullToLoadListener {
    /**
     * 加载更新
     */
    void onLoadNew();

    /**
     * 加载更多
     */
    void onLoadMore();
}
