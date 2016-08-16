package com.droidworker.lib.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.R;
import com.droidworker.lib.constant.Orientation;
import com.droidworker.lib.constant.State;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class LoadingLayout extends FrameLayout implements ILoadingLayout {
    private Orientation mOrientation;
    private LinearLayout mContainer;
    private ImageView mImageView;
    private TextView mTextView;

    public LoadingLayout(Context context,  Orientation orientation) {
        super(context);

        mOrientation = orientation;
        switch (mOrientation){
            case VERTICAL:
                default:
                    LayoutInflater.from(context).inflate(R.layout.layout_loading_default, this, true);
                break;
            case HORIZONTAL:
                break;
        }
        mContainer = (LinearLayout) findViewById(R.id.ll_loading_container);
        mImageView = (ImageView) findViewById(R.id.iv_loading_img);
        mTextView = (TextView) findViewById(R.id.tv_loading_text);
    }

    @Override
    public int getSize() {
        return mContainer.getHeight();
    }

    @Override
    public void onPull(State state, float distance) {

    }
}
