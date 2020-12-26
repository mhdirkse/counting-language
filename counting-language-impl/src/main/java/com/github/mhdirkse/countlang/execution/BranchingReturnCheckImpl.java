/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    @Override
    public void onRepetitionOpened() {
    }

    @Override
    public void onRepetitionClosed() {
    }
}
