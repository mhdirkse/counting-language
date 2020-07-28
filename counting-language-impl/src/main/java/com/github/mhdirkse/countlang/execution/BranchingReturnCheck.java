package com.github.mhdirkse.countlang.execution;

/**
 * This interface checks whether all branches within a function
 * return a value. When a function contains an if-statement, then
 * return statements may only appear in the then and the else
 * blocks. If both have the return, then the function returns and
 * no statements may appear after the if.
 * <p>
 * Instances of this class must be fed by switch open/close
 * events and branch open/close events and also with
 * return statement occurrences. Then the state according
 * to {@link com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status}
 * is maintained.
 * <p>
 * This interface can also help by finding statements after return
 * statements. If within a branch, a statement appears after the
 * return, then the status should be ALL_RETURN at that moment.
 * To achieve this, the state applies to the current branch.
 * <p>
 * If a branch is opened and
 * then a return appears, the status is "ALL_RETURN". If the branch
 * is closed and another branch opens, the state becomes "NO_RETURN",
 * because the new branch does not have a return statement. When
 * this branch closes and the switching closes, the state appears
 * of the combination of the two branches. That means: "SOME_RETURN".
 * At this point, we can thus check whether the branches within
 * a function do/dont return consistently.
 * <p>
 * Asking the status after a branch is closed but before switching
 * is closed is not supported.
 * <p>
 * This interface is to be used for checking counting-language code.
 * No more statements should appear after a return. Therefore,
 * it is assumed that switching only starts when the status is
 * NO_RETURN or SOME_RETURN.
 * 
 * @author martijn
 *
 */
public interface BranchingReturnCheck extends BranchHandler {
    public enum Status {
        NO_RETURN,
        SOME_RETURN,
        ALL_RETURN;
    }
    
    void onReturn();
}
