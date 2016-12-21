package com.droidworker.pulltoloadview.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EdgeEffect;

/**
 * 将系统提供的{@link EdgeEffect}封装进view，以便在{@link com.droidworker.pulltoloadview.PullToLoadBaseView}中使用
 * 如果直接在canvas上绘制会使得效果在内容下方，改成view可以把层级提上去。
 *
 * @author luoyanfeng@le.com
 */

public class EdgeEffectView extends View {
    /**
     * 垂直类型
     */
    public static final int TYPE_VERTICAL = 100;
    /**
     * 水平类型
     */
    public static final int TYPE_HORIZONTAL = 101;
    /**
     * 类型，是在上边下边还是左边右边
     */
    private int mType;
    /**
     * Edge effect颜色
     */
    private int mColor;
    private EdgeEffectCompat mStartEdgeEffect = new EdgeEffectCompat(getContext());
    private EdgeEffectCompat mEndEdgeEffect = new EdgeEffectCompat(getContext());

    public EdgeEffectView(Context context) {
        super(context);
    }

    public EdgeEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setType(@IntRange(from = TYPE_VERTICAL, to = TYPE_HORIZONTAL) int type) {
        mType = type;
    }

    /**
     * {@link EdgeEffect#setSize(int, int)}
     */
    public void setSize(int width, int height) {
        mStartEdgeEffect.setSize(width, height);
        mEndEdgeEffect.setSize(width, height);
    }

    /**
     * {@link EdgeEffect#isFinished()}
     */
    public boolean isFinished() {
        return mStartEdgeEffect.isFinished() && mEndEdgeEffect.isFinished();
    }

    /**
     * {@link EdgeEffect#onPull(float, float)}
     */
    public void onPullStart(float deltaDistance, float displacement) {
        mStartEdgeEffect.onPull(deltaDistance, displacement);
    }

    /**
     * {@link EdgeEffect#onPull(float, float)}
     */
    public void onPullEnd(float deltaDistance, float displacement) {
        mEndEdgeEffect.onPull(deltaDistance, displacement);
    }

    /**
     * {@link EdgeEffect#onRelease()}
     */
    public void onRelease() {
        if (!mStartEdgeEffect.isFinished()) {
            mStartEdgeEffect.onRelease();
        }
        if (!mEndEdgeEffect.isFinished()) {
            mEndEdgeEffect.onRelease();
        }
    }

    /**
     * {@link EdgeEffect#getColor()}
     */
    @ColorInt
    public int getColor() {
        return mColor;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final int width = getWidth();
        final int height = getHeight();
        boolean needsInvalidate = false;

        //参照了RecyclerView的绘制部分
        switch (mType) {
            case TYPE_VERTICAL:
            default:
                if (!mStartEdgeEffect.isFinished()) {
                    final int restoreCount = canvas.save();
                    needsInvalidate = mStartEdgeEffect.draw(canvas);
                    canvas.restoreToCount(restoreCount);
                }
                if (!mEndEdgeEffect.isFinished()) {
                    final int restoreCount = canvas.save();
                    canvas.rotate(180);
                    canvas.translate(-width, -height);
                    needsInvalidate |= mEndEdgeEffect.draw(canvas);
                    canvas.restoreToCount(restoreCount);
                }
                break;
            case TYPE_HORIZONTAL:
                if (!mStartEdgeEffect.isFinished()) {
                    final int restoreCount = canvas.save();
                    canvas.rotate(270);
                    canvas.translate(-height, 0);
                    needsInvalidate = mStartEdgeEffect.draw(canvas);
                    canvas.restoreToCount(restoreCount);
                }
                if (!mEndEdgeEffect.isFinished()) {
                    final int restoreCount = canvas.save();
                    canvas.rotate(90);
                    canvas.translate(0, -width);
                    needsInvalidate |= mEndEdgeEffect.draw(canvas);
                    canvas.restoreToCount(restoreCount);
                }
                break;
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
