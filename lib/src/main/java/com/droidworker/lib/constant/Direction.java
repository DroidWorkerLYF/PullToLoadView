package com.droidworker.lib.constant;

/**
 * 代表方向的枚举
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
