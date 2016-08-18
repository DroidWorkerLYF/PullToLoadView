package com.droidworker.lib;

/**
 * @author luoyanfeng@le.com
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
