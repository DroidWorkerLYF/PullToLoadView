package com.droidworker.pulltoloadview;

import com.droidworker.lib.recyclerview.BaseRecyclerViewAdapter;
import com.droidworker.lib.recyclerview.UniversalViewHolder;

import android.widget.TextView;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class Adapter extends BaseRecyclerViewAdapter {
    private int mCount = 30;

    public Adapter() {
        setLayoutId(R.layout.layout_item);
    }

    @Override
    protected void bindData(UniversalViewHolder universalViewHolder, int position) {
        TextView textView = universalViewHolder.findViewById(R.id.text);
        textView.setText(String.valueOf(position + 1));
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
