package com.github.mhdirkse.countlang.analysis;

import java.util.Comparator;

/**
 * The order of the values is significant. If in a serial block, every child
 * has status NO_RETURN, then the block's status is NO_RETURN. If any child's
 * status is SOME_RETURN while the other children have NONE_RETURN, then the block's
 * status is SOME_RETURN. The same applies to the other values.
 * @author martijn
 *
 */
enum ReturnStatus implements Comparable<ReturnStatus> {
    NONE_RETURN,
    SOME_RETURN,
    WEAK_ALL_RETURN,
    STRONG_ALL_RETURN;

    static final Comparator<ReturnStatus> COMPARATOR = new Comparator<ReturnStatus>() {
        @Override
        public int compare(final ReturnStatus r1, final ReturnStatus r2) {
            return r1.compareTo(r2);
        }
    };
}
