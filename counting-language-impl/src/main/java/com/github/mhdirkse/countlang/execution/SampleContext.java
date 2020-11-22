package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.types.Distribution;

public interface SampleContext extends SampleContextBase {
    public void score(int value);
    public void scoreUnknown();
    public Distribution getResult();
    public boolean isScored();

    public static SampleContext getInstance() {
        return new SampleContextImpl();
    }
}
