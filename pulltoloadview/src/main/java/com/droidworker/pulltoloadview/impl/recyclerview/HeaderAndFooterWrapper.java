package com.droidworker.pulltoloadview.impl.recyclerview;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 将Adapter包装成支持header和footer的HeaderAndFooterWrapper,处理里不同LayoutManager下header和footer的
 * 正常显示.
 * @author https://github.com/DroidWorkerLYF
 */
public class HeaderAndFooterWrapper extends RecyclerView.Adapter {
    private static final int TYPE_HEADER = 10000;
    private static final int TYPE_FOOTER = 10001;
    private SparseArrayCompat<View> mHeaders = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooters = new SparseArrayCompat<>();
    private RecyclerView.Adapter mWrappedAdapter;

    public HeaderAndFooterWrapper() {

    }

    public HeaderAndFooterWrapper(RecyclerView.Adapter adapter) {
        mWrappedAdapter = adapter;
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

    public void setWrappedAdapter(@NonNull RecyclerView.Adapter adapter) {
        mWrappedAdapter = adapter;
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return mWrappedAdapter;
    }

    public void addHeader(View header) {
        mHeaders.put(mHeaders.size() + TYPE_HEADER, header);
        notifyDataSetChanged();
    }

    public void addFooter(View footer) {
        mFooters.put(mFooters.size() + TYPE_FOOTER, footer);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaders.get(viewType) != null) {
            return new HeaderFooterViewHolder(mHeaders.get(viewType));
        } else if (mFooters.get(viewType) != null) {
            return new HeaderFooterViewHolder(mFooters.get(viewType));
        }
        return mWrappedAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position) || isFooter(position) || mWrappedAdapter == null) {
            return;
        }
        // noinspection unchecked
        mWrappedAdapter.onBindViewHolder(holder, position - getHeaderCount());
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getWrappedItemCount() + getFooterCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mWrappedAdapter.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    final int viewType = getItemViewType(position);
                    if (mHeaders.get(viewType) != null || mFooters.get(viewType) != null) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        // noinspection unchecked
        mWrappedAdapter.onViewAttachedToWindow(holder);
        final int position = holder.getAdapterPosition();
        if (!isHeader(position) || !isFooter(position)) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null
                && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
        }
    }

    private class HeaderFooterViewHolder extends RecyclerView.ViewHolder {

        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
