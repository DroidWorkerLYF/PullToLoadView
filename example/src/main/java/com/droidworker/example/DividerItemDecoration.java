package com.droidworker.example;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Google给出的DividerItemDecoration.支持水平和垂直方向的分割线绘制.
 * <a href=
 * "https://android.googlesource.com/platform/development/+/master/samples/Support7Demos/src/com/example/android/supportv7/widget/decorator/DividerItemDecoration.java#101"
 * >Google Demo</a>
 * 增加了颜色和大小,增加了支持网格
 * @author luoyanfeng@le.com
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    public static final int GRID = 2;
    private Drawable mDivider;
    private int mOrientation;
    private int mWidth;
    private int mHeight;

    public DividerItemDecoration(int orientation, int color, int size) {
        this(orientation, color, size, size);
    }

    public DividerItemDecoration(int orientation, int color, int vertical, int horizontal){
        setOrientation(orientation);
        mDivider = new ColorDrawable(color);
        mWidth = horizontal;
        mHeight = vertical;
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST && orientation != GRID) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else if(mOrientation == HORIZONTAL_LIST){
            drawHorizontal(c, parent);
        } else if(mOrientation == GRID){
            drawVertical(c, parent);
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin
                    + Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + mHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin
                    + Math.round(ViewCompat.getTranslationX(child));
            final int right = left + mWidth;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
            RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mHeight);
        } else if(mOrientation == HORIZONTAL_LIST){
            outRect.set(0, 0, mWidth, 0);
        } else if(mOrientation == GRID){
            outRect.set(0, 0, mWidth, mHeight);
        }
    }

    public int getOrientation() {
        return mOrientation;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
