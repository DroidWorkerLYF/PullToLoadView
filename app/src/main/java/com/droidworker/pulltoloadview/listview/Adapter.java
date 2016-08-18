package com.droidworker.pulltoloadview.listview;

import com.droidworker.pulltoloadview.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author luoyanfeng@le.com
 */
public class Adapter extends BaseAdapter {
    private int mCount = 10;
    private String[] mTitle;
    private String[] mContent;

    public Adapter(String[] title, String[] content) {
        mTitle = title;
        mContent = content;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item,
                    parent, false);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.title);
            viewHolder.subTitleView = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.titleView.setText(mTitle[position % 3]);
        viewHolder.subTitleView.setText(mContent[position % 3]);
        return convertView;
    }

    public void updateCount() {
        mCount += 10;
        notifyDataSetChanged();
    }

    public void restoreCount() {
        mCount = 10;
    }

    class ViewHolder {
        TextView titleView;
        TextView subTitleView;
    }
}
