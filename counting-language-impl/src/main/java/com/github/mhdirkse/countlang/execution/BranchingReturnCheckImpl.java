package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status.ALL_RETURN;
import static com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status.NO_RETURN;
import static com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status.SOME_RETURN;

import java.util.ArrayList;
import java.util.List;

public class BranchingReturnCheckImpl implements BranchingReturnCheck {
    private Status status = NO_RETURN;
    private List<Status> branchStatuses = new ArrayList<>();
    private BranchingReturnCheckImpl currentBranch = null;
    private boolean inSwitch = false;

    @Override
    public void onSwitchOpened() {
        if(currentBranch == null) {
            if(status == ALL_RETURN) {
                throw new IllegalStateException("Statements after a return are not supported");
            }
            inSwitch = true;
        }
        else {
            currentBranch.onSwitchOpened();
        }
    }

    @Override
    public void onSwitchClosed() {
        if(currentBranch == null) {
            updateStatusAfterSwitch();
            branchStatuses = new ArrayList<>();
            inSwitch = false;
        }
        else {
            currentBranch.onSwitchClosed();
        }
    }

    private void updateStatusAfterSwitch() {
        boolean noReturn = branchStatuses
                .stream()
                .allMatch(s -> s == NO_RETURN);
        boolean allReturn = branchStatuses
                .stream()
                .allMatch(s -> s == ALL_RETURN);
        if(allReturn) {
            status = ALL_RETURN;
        } else if(noReturn) {
            status = NO_RETURN;
        } else {
            status = SOME_RETURN;
        }
    }

    @Override
    public void onBranchOpened() {
        if(currentBranch == null) {
            currentBranch = new BranchingReturnCheckImpl();
        } else {
            currentBranch.onBranchOpened();
        }
    }

    @Override
    public void onBranchClosed() {
        if(currentBranch.inSwitch) {
            currentBranch.onBranchClosed();
        } else {
            branchStatuses.add(currentBranch.getStatus());
            currentBranch = null;            
        }
    }

    @Override
    public void onReturn() {
        if(currentBranch == null) {
            status = ALL_RETURN;
        } else {
            currentBranch.onReturn();
        }
    }

    public Status getStatus() {
        if(currentBranch != null) {
            return currentBranch.getStatus();
        } else if(inSwitch) {
            throw new IllegalStateException("Requesting the status in-between branches is not supported");
        } else {
            return status;
        }
    }
}
