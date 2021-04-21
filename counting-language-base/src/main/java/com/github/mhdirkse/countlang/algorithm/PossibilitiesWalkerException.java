package com.github.mhdirkse.countlang.algorithm;

import lombok.Getter;

class PossibilitiesWalkerException extends Exception {
    private static final long serialVersionUID = 3546479381302208644L;

    PossibilitiesWalkerException(String msg) {
        super(msg);
    }

    static class NewDistributionDoesNotFitParentCount extends PossibilitiesWalkerException {
        private static final long serialVersionUID = 1387301468038442369L;
        private final @Getter int childCount;
        private final @Getter int parentCount;

        NewDistributionDoesNotFitParentCount(int childCount, int parentCount) {
            super(String.format("Added distribution's count %d does not fit in parent count %d", childCount, parentCount));
            this.childCount = childCount;
            this.parentCount = parentCount;
        }
    }
}
