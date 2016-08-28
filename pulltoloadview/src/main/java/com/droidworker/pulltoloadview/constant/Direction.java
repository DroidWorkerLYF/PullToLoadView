package com.droidworker.pulltoloadview.constant;

/**
 * 代表方向的枚举,枚举的取值和{@link android.support.v4.view.ViewCompat}中判断滚动时使用的一致
 * @author https://github.com/DroidWorkerLYF
 */
public enum Direction {
    /**
     * 视图还能不能向上(左)滑动
     */
    END(1),
    /**
     * 视图还能不能向下(右)滑动
     */
    START(-1);

    private int mIntValue;

    Direction(int intValue) {
        mIntValue = intValue;
    }

    public int getIntValue() {
        return mIntValue;
    }
}
