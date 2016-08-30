package com.droidworker.example.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@link RecyclerView}的通用ViewHolder
 * @author https://github.com/DroidWorkerLYF
 */
public class UniversalViewHolder extends RecyclerView.ViewHolder {
    /**
     * 用于存储id和view的对应关系
     */
    private SparseArray<View> mViews;

    public UniversalViewHolder(View itemView) {
        super(itemView);

        mViews = new SparseArray<>();
    }

    public static UniversalViewHolder getViewHolder(ViewGroup viewGroup,
            int layoutId) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layoutId, viewGroup, false);
        return new UniversalViewHolder(view);
    }

    public <T extends View> T findViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        // noinspection unchecked
        return (T) view;
    }
}
