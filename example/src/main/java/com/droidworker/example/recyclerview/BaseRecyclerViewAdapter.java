package com.droidworker.example.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecyclerView的通用基础Adapter,封装了onCreateViewHolder方法,子类只需要实现
 * {@link #bindViewHolder}方法即可,
 * 无需再重复创造视图的代码.
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class BaseRecyclerViewAdapter
        extends RecyclerView.Adapter<UniversalViewHolder> {
    /**
     * 存储ViewType和LayoutId对应关系,在Adapter需要多种视图时使用
     */
    private SparseIntArray mTypeLayoutIdArray = new SparseIntArray();

    public BaseRecyclerViewAdapter() {

    }

    /**
     * 单一布局构造函数
     * @param layoutId
     *            单一布局id
     */
    public BaseRecyclerViewAdapter(@LayoutRes int layoutId) {
        setLayoutId(layoutId);
    }

    /**
     * 多类型布局构造函数
     * @param typeLayoutIdArray
     *            ViewType和LayoutId的对应关系
     */
    public BaseRecyclerViewAdapter(SparseIntArray typeLayoutIdArray) {
        setTypeLayoutIdArray(typeLayoutIdArray);
    }

    @Override
    public UniversalViewHolder onCreateViewHolder(ViewGroup viewGroup,
            int viewType) {
        final int layoutId = mTypeLayoutIdArray.get(viewType, 0);
        if (layoutId == 0) {
            return new UniversalViewHolder(new View(viewGroup.getContext()));
        }
        return UniversalViewHolder.getViewHolder(viewGroup, layoutId);
    }

    @Override
    public void onBindViewHolder(UniversalViewHolder universalViewHolder,
            int position) {
        bindData(universalViewHolder, position);
    }

    protected abstract void bindData(UniversalViewHolder universalViewHolder,
            int position);

    public void setLayoutId(@LayoutRes int layoutId) {
        mTypeLayoutIdArray.put(0, layoutId);
    }

    public void setTypeLayoutIdArray(SparseIntArray typeLayoutIdArray) {
        if (typeLayoutIdArray == null) {
            return;
        }
        mTypeLayoutIdArray = typeLayoutIdArray;
    }
}
