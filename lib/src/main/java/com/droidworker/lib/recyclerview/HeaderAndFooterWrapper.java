package com.droidworker.lib.recyclerview;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 为BaseRecyclerViewAdapter增加Header和Footer相关的方法.
 * @author luoyanfeng@le.com
 */
public class HeaderAndFooterWrapper extends BaseRecyclerViewAdapter {
    private static final int TYPE_HEADER = 10000;
    private static final int TYPE_FOOTER = 10001;

    private SparseArrayCompat<View> mHeaders = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooters = new SparseArrayCompat<>();
    private BaseRecyclerViewAdapter mWrappedAdapter;

    public HeaderAndFooterWrapper(
            BaseRecyclerViewAdapter baseRecyclerViewAdapter) {
        mWrappedAdapter = baseRecyclerViewAdapter;
    }

    public boolean isHeader(int position) {
        return position < getHeaderCount();
    }

    public boolean isFooter(int position) {
        return position >= getHeaderCount() + getWrappedItemCount();
    }

    public boolean containsHeader(View header) {
        return mHeaders.indexOfValue(header) > -1;
    }

    public boolean containsFooter(View footer) {
        return mFooters.indexOfValue(footer) > -1;
    }

    public int getHeaderCount() {
        return mHeaders.size();
    }

    public int getFooterCount() {
        return mFooters.size();
    }

    public int getWrappedItemCount() {
        if (mWrappedAdapter == null) {
            return 0;
        }
        return mWrappedAdapter.getItemCount();
    }

    public BaseRecyclerViewAdapter getWrappedAdapter() {
        return mWrappedAdapter;
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getWrappedItemCount() + getFooterCount();
    }

    public void addHeader(View header) {
        mHeaders.put(mHeaders.size() + TYPE_HEADER, header);
        notifyDataSetChanged();
    }

    public void addFooter(View header) {
        mFooters.put(mFooters.size() + TYPE_FOOTER, header);
        notifyDataSetChanged();
    }

    public void removeHeader(View header) {
        final int position = mHeaders.indexOfValue(header);
        if (position != -1) {
            mHeaders.removeAt(position);
        }
    }

    public void removeFooter(View footer) {
        final int position = mFooters.indexOfValue(footer);
        if (position != -1) {
            mFooters.removeAt(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return mHeaders.keyAt(position);
        } else if (isFooter(position)) {
            return mFooters.keyAt(position - getHeaderCount() - getWrappedItemCount());
        }
        return mWrappedAdapter.getItemViewType(position - getHeaderCount());
    }

    @Override
    public UniversalViewHolder onCreateViewHolder(ViewGroup viewGroup,
            int viewType) {
        if (mHeaders.get(viewType) != null) {
            return new UniversalViewHolder(mHeaders.get(viewType));
        } else if (mFooters.get(viewType) != null) {
            return new UniversalViewHolder(mFooters.get(viewType));
        }
        return mWrappedAdapter.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(UniversalViewHolder universalViewHolder,
            int position) {
        if (isHeader(position) || isFooter(position)) {
            return;
        }
        mWrappedAdapter.onBindViewHolder(universalViewHolder,
                position - getHeaderCount());
    }

    @Override
    protected void bindData(UniversalViewHolder universalViewHolder,
            int position) {

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mWrappedAdapter.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView
                .getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager
                    .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            final int viewType = getItemViewType(position);
                            if (mHeaders.get(viewType) != null
                                    || mFooters.get(viewType) != null) {
                                return gridLayoutManager.getSpanCount();
                            }
                            return 1;
                        }
                    });
        }
    }

    @Override
    public void onViewAttachedToWindow(UniversalViewHolder holder) {
        mWrappedAdapter.onViewAttachedToWindow(holder);
        final int position = holder.getPosition();
        if (!isHeader(position) || !isFooter(position)) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null
                && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) layoutParams)
                    .setFullSpan(true);
        }
    }
}
