package com.droidworker.pulltoloadview;

import com.droidworker.lib.recyclerview.BaseRecyclerViewAdapter;
import com.droidworker.lib.recyclerview.UniversalViewHolder;

import android.widget.TextView;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class Adapter extends BaseRecyclerViewAdapter {
    private int mCount = 30;
    private String[] mTitle;
    private String[] mContent;

    public Adapter(String[] title, String[] content) {
        setLayoutId(R.layout.layout_item);
        mTitle = title;
        mContent = content;
    }

    @Override
    protected void bindData(UniversalViewHolder universalViewHolder, int position) {
        TextView titleView = universalViewHolder.findViewById(R.id.title);
        TextView subtitleView = universalViewHolder.findViewById(R.id.subtitle);

        titleView.setText(mTitle[position % 3]);
        subtitleView.setText(mContent[position % 3]);
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void updateCount() {
        mCount += 15;
        notifyDataSetChanged();
    }

    public void restoreCount(){
        mCount = 30;
    }
}
