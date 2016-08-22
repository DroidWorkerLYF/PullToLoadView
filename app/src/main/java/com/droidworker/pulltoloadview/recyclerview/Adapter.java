package com.droidworker.pulltoloadview.recyclerview;

import android.widget.TextView;

import com.droidworker.lib.recyclerview.BaseRecyclerViewAdapter;
import com.droidworker.lib.recyclerview.UniversalViewHolder;
import com.droidworker.pulltoloadview.R;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class Adapter extends BaseRecyclerViewAdapter {
    private int mCount = 10;
    private String[] mTitle;
    private String[] mContent;

    public Adapter(String[] title, String[] content, boolean vertical) {
        if(vertical){
            setLayoutId(R.layout.layout_item_v);
        } else {
            setLayoutId(R.layout.layout_item_h);
        }
        mTitle = title;
        mContent = content;
    }

    @Override
    protected void bindData(UniversalViewHolder universalViewHolder, int position) {
        TextView titleView = universalViewHolder.findViewById(R.id.title);
        TextView subTitleView = universalViewHolder.findViewById(R.id.subtitle);

        titleView.setText(mTitle[position % 3]);
        subTitleView.setText(mContent[position % 3]);
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void updateCount() {
        mCount += 10;
        notifyDataSetChanged();
    }

    public void restoreCount() {
        mCount = 10;
        notifyDataSetChanged();
    }

    public void clear(){
        mCount = 0;
        notifyDataSetChanged();
    }
}
