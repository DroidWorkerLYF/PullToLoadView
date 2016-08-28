package com.droidworker.pulltoloadview.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droidworker.pulltoloadview.R;
import com.droidworker.pulltoloadview.ILoadingLayout;
import com.droidworker.pulltoloadview.constant.Orientation;
import com.droidworker.pulltoloadview.constant.State;

/**
 * 实现ILoadingLayout接口,用于header和footer的默认视图
 * @author https://github.com/DroidWorkerLYF
 */
public class LoadingLayout extends FrameLayout implements ILoadingLayout {
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
     * 方向
     */
    private Orientation mOrientation;
    /**
     * 旋转动画
     */
    private RotateAnimation mRotateAnimation;

    public LoadingLayout(Context context) {
        this(context, Orientation.VERTICAL);
    }

    public LoadingLayout(Context context, Orientation orientation) {
        super(context);
        mOrientation = orientation;
        //根据方向inflate不同的布局
        switch (orientation) {
        case VERTICAL:
        default:
            LayoutInflater.from(context).inflate(R.layout.layout_loading_vertical, this, true);
            break;
        case HORIZONTAL:
            LayoutInflater.from(context).inflate(R.layout.layout_loading_horizontal, this, true);
            break;
        }
        //find views
        mContainer = (LinearLayout) findViewById(R.id.ll_loading_container);
        mImageView = (ImageView) findViewById(R.id.iv_loading_img);
        mTextView = (TextView) findViewById(R.id.tv_loading_text);
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
        //根据方向,判断返回高度或者宽度
        switch (mOrientation) {
        case VERTICAL:
        default:
            return mContainer.getHeight();
        case HORIZONTAL:
            return mContainer.getWidth();
        }
    }

    @Override
    public void onPull(State state, float distance) {
        if (state == State.UPDATING || state == State.MANUAL_UPDATE) {
            //加载更新或者自动更新状态
            mImageView.startAnimation(mRotateAnimation);
            mTextView.setText(R.string.updating);
        } else if (state == State.LOADING) {
            //加载更多状态
            mImageView.startAnimation(mRotateAnimation);
            mTextView.setText(R.string.loading);
        } else if (state == State.RESET) {
            //reset,停止动画
            mImageView.clearAnimation();
        } else {
            if (state == State.PULL_FROM_START) {
                //下拉
                mTextView.setText(R.string.pull_to_update);
            } else if (state == State.PULL_FROM_END) {
                //上拉
                mTextView.setText(R.string.pull_to_load);
            } else if (state == State.RELEASE_TO_UPDATE) {
                //释放来加载更新
                mTextView.setText(R.string.release_to_update);
            } else if (state == State.RELEASE_TO_LOAD) {
                //释放来加载更多
                mTextView.setText(R.string.release_to_load);
            }
            if (distance != 0) {
                mImageView.setRotation(Math.abs(distance) % getSize() / 100 * 360f);
            }
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
