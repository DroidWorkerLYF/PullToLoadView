package com.droidworker.lib.constant;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public enum LoadMode {
    PULL_FROM_START,
    PULL_FROM_START_AUTO_LOAD,
    PULL_FROM_END,
    BOTH,
    DISABLED,
    MANUAL_ONLY;

    public boolean isPullToLoad(){
        return !(this == DISABLED || this == MANUAL_ONLY);
    }

    public boolean isPullFromStart(){
        return this == PULL_FROM_START || this == BOTH || this == PULL_FROM_START_AUTO_LOAD;
    }

    public boolean isPullFromEnd(){
        return this == PULL_FROM_END || this == BOTH;
    }
}
