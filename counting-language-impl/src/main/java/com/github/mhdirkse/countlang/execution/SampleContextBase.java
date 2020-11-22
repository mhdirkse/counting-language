package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.types.Distribution;

public interface SampleContextBase {
    public void startSampledVariable(final Distribution sampledDistribution);
    public void stopSampledVariable();
    public boolean hasNextValue();
    public int nextValue();

}
