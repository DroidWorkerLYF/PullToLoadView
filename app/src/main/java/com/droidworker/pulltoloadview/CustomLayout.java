package com.droidworker.pulltoloadview;

import com.droidworker.lib.ILoadingLayout;
import com.droidworker.lib.constant.State;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 测试支持自定义header使用的简单layout,只支持竖直方向.
 * @author https://github.com/DroidWorkerLYF
 */
public class CustomLayout extends FrameLayout implements ILoadingLayout {
    /**
     * 布局容器
     */
    private LinearLayout mContainer;
    /**
     * 加载进度
     */
    private ImageView mImageView;
    /**
     * 加载文案
     */
    private TextView mTextView;
    /**
     * 旋转动画
     */
    private RotateAnimation mRotateAnimation;

    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //find views
        mContainer = (LinearLayout) findViewById(com.droidworker.lib.R.id.ll_loading_container);
        mImageView = (ImageView) findViewById(com.droidworker.lib.R.id.iv_loading_img);
        mTextView = (TextView) findViewById(com.droidworker.lib.R.id.tv_loading_text);
        //create animation
        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    public int getSize() {
        return mContainer.getHeight();
    }

    @Override
    public void onPull(State state, float distance) {
        if (state == State.UPDATING || state == State.MANUAL_UPDATE) {
            //加载更新或者自动更新状态
            mImageView.startAnimation(mRotateAnimation);
            mTextView.setText(com.droidworker.lib.R.string.updating);
        } else if (state == State.LOADING) {
            //加载更多状态
            mImageView.startAnimation(mRotateAnimation);
            mTextView.setText(com.droidworker.lib.R.string.loading);
        } else if (state == State.RESET) {
            //reset,停止动画
            mImageView.clearAnimation();
        } else if(distance != 0){
            mImageView.setRotation(Math.abs(distance) % getSize() / 100 * 360f);
            mTextView.setText(String.valueOf(distance));
        }
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(INVISIBLE);
    }

    @Override
    public View getLoadingView() {
        return this;
    }
}
