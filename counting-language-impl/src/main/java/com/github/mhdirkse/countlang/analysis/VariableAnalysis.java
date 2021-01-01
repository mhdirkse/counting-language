package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.analysis.VariableWriteStatus.Type;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

abstract class VariableAnalysis implements VariableEvent.Visitor {
    static class WriteStatus implements Iterable<AccessEvent> {
        Map<AccessEvent, VariableWriteStatus> writeStatuses;

        WriteStatus() {
            writeStatuses = new HashMap<>();
        }

        WriteStatus(Map<AccessEvent, VariableWriteStatus> writeStatusus) {
            this.writeStatuses = writeStatusus;
        }

        WriteStatus(WriteStatus oldStatus, WriteStatus newStatus) {
            writeStatuses = new HashMap<>();
            writeStatuses.putAll(newStatus.writeStatuses);
            Set<AccessEvent> remainingOld = new HashSet<>(oldStatus.writeStatuses.keySet());
            remainingOld.removeAll(newStatus.writeStatuses.keySet());
            for(AccessEvent ev: remainingOld) {
                writeStatuses.put(ev, oldStatus.writeStatuses.get(ev));
            }
        }

        boolean isEmpty() {
            return writeStatuses.isEmpty();
        }

        @Override
        public Iterator<AccessEvent> iterator() {
            return writeStatuses.keySet().iterator();
        }

        boolean contains(AccessEvent event) {
            return writeStatuses.containsKey(event);
        }

        VariableWriteStatus get(AccessEvent event) {
            return writeStatuses.get(event);
        }

        void add(VariableWriteStatus status) {
            writeStatuses.put(status.getOriginalWrite(), status);
        }

        void remove(AccessEvent event) {
            writeStatuses.remove(event);
        }

        Set<AccessEvent> keys() {
            return writeStatuses.keySet();
        }
    }

    static class WriteUpdates {
        Map<AccessEvent, VariableWriteStatus> unchanged;
        Map<AccessEvent, VariableWriteStatus> newWrites;
        Map<AccessEvent, VariableWriteStatus> updatedWrites;

        WriteUpdates(WriteStatus existingStatus, WriteStatus newStatus) {
            Set<AccessEvent> unchangedEvents = new HashSet<>(existingStatus.keys());
            unchangedEvents.removeAll(newStatus.keys());
            Set<AccessEvent> newEvents = new HashSet<>(newStatus.keys());
            newEvents.removeAll(existingStatus.keys());
            Set<AccessEvent> updatedEvents = new HashSet<>(existingStatus.keys());
            updatedEvents.retainAll(newStatus.keys());
            unchanged = new HashMap<>();
            for(AccessEvent ev: unchangedEvents) {
                unchanged.put(ev, existingStatus.get(ev));
            }
            newWrites = new HashMap<>();
            for(AccessEvent ev: newEvents) {
                newWrites.put(ev, newStatus.get(ev));
            }
            updatedWrites = new HashMap<>();
            for(AccessEvent ev: updatedEvents) {
                updatedWrites.put(ev, newStatus.get(ev));
            }
        }
    }

    final AnalyzedVariable variable;
    int eventIndex;
    WriteStatus existingStatus;
    WriteStatus newStatus = new WriteStatus();

    VariableAnalysis(AnalyzedVariable variable, int eventIndex, WriteStatus existingStatus) {
        this.variable = variable;
        this.eventIndex = eventIndex;
        this.existingStatus = existingStatus;
    }

    abstract void run();

    void handleNextEvent() {
        variable.getEvent(eventIndex++).accept(this);
    }

    abstract boolean isDefault();

    @Override
    public void visitAccessEvent(AccessEvent event) {
        switch(event.getEventType()) {
        case READ:
            handleRead(event);
            break;
        case WRITE:
        case DEFINE:
            handleWrite(event);
            break;
        }
    }

    private void handleRead(AccessEvent readEvent) {
        for(AccessEvent writeEvent: newStatus) {
            if(newStatus.get(writeEvent).getType() == Type.NEW) {
                newStatus.remove(writeEvent);
                newStatus.add(new VariableWriteStatus.VariableWriteStatusUsed(writeEvent));
            }
        }
        for(AccessEvent writeEvent: existingStatus) {
            if(existingStatus.get(writeEvent).getType() == Type.NEW) {
                newStatus.add(new VariableWriteStatus.VariableWriteStatusUsed(writeEvent));
            }
        }
    }

    private void handleWrite(AccessEvent newWriteEvent) {
        for(AccessEvent event: newStatus) {
            if(newStatus.get(event).getType() == Type.NEW) {
                newStatus.remove(event);
                newStatus.add(new VariableWriteStatus.VariableWriteStatusOverwritten(event, newWriteEvent.getLine(), newWriteEvent.getColumn()));
            }
        }
        for(AccessEvent event: existingStatus) {
            if(existingStatus.get(event).getType() == Type.NEW) {
                newStatus.add(new VariableWriteStatus.VariableWriteStatusOverwritten(event, newWriteEvent.getLine(), newWriteEvent.getColumn()));
            }
        }
        newStatus.add(new VariableWriteStatus.VariableWriteStatusNew(newWriteEvent));
    }

    @Override
    public void visitControlEvent(ControlEvent event) {
        switch(event.getEventType()) {
        case SWITCH_OPEN:
            handleSwitchOpen(event);
            break;
        case SWITCH_CLOSE:
            handleSwitchClose(event);
            break;
        case BRANCH_OPEN:
            handleBranchOpen(event);
            break;
        case BRANCH_CLOSE:
            handleBranchClose(event);
            break;
        case REPETITION_START:
            handleRepetitionStart(event);
            break;
        case REPETITION_STOP:
            handleRepetitionStop(event);
            break;
        case RETURN:
        case STATEMENT_OPEN:
        case STATEMENT_CLOSE:
            break;
        }
    }

    void handleSwitchOpen(ControlEvent event) {
        Switch switchHandler = new Switch(variable, eventIndex, new WriteStatus(existingStatus, newStatus));
        switchHandler.run();
        eventIndex = switchHandler.eventIndex;
        newStatus = switchHandler.newStatus;
    }

    void handleSwitchClose(ControlEvent event) {
    }

    void handleBranchOpen(ControlEvent event) {
    }

    void handleBranchClose(ControlEvent event) {
    }

    void handleRepetitionStart(ControlEvent event) {
        
    }

    void handleRepetitionStop(ControlEvent event) {
        
    }

    static class Default extends VariableAnalysis {
        private final StatusReporter reporter;

        Default(AnalyzedVariable variable, StatusReporter reporter) {
            super(variable, 0, new WriteStatus());
            this.reporter = reporter;
        }

        @Override
        void run() {
            while(eventIndex < variable.getNumEvents()) {
                handleNextEvent();
            }
            // TODO: Report all remaining new writes
            // TODO: Report all overwritten writes
        }

        @Override
        boolean isDefault() {
            return true;
        }
    }

    static class Switch extends VariableAnalysis {
        boolean stop = false;
        List<Branch> branches = new ArrayList<>();

        Switch(AnalyzedVariable variable, int eventIndex, WriteStatus existingStatus) {
            super(variable, eventIndex, existingStatus);
        }

        @Override
        void run() {
            while(! stop) {
                handleNextEvent();
            }
        }

        @Override
        void handleSwitchClose(ControlEvent event) {
            stop = true;
            List<Branch> updatingBranches = branches.stream()
                    .filter(branch -> ! branch.newStatus.isEmpty())
                    .collect(Collectors.toList());
            List<WriteUpdates> branchUpdates = updatingBranches.stream()
                    .map(branch -> new WriteUpdates(branch.existingStatus, branch.newStatus))
                    .collect(Collectors.toList());
            calculateNewStatus(branchUpdates);
        }

        private void calculateNewStatus(List<WriteUpdates> branchUpdates) {
            Map<AccessEvent, VariableWriteStatus> newStatusSeed = new HashMap<>();
            branchUpdates.forEach(update -> newStatusSeed.putAll(update.newWrites));
            Set<AccessEvent> remaining = new HashSet<>();
            for(WriteUpdates branchWriteUpdate: branchUpdates) {
                remaining.addAll(branchWriteUpdate.unchanged.keySet());
                remaining.addAll(branchWriteUpdate.updatedWrites.keySet());
            }
            for(AccessEvent event: remaining) {
                newStatusSeed.put(event, getNewStatusForAccessEvent(event, branchUpdates));
            }
            newStatus = new WriteStatus(newStatusSeed);
        }

        private VariableWriteStatus getNewStatusForAccessEvent(AccessEvent event, List<WriteUpdates> branchUpdates) {
            VariableWriteStatus unchanged = testAndGetUnchanged(event, branchUpdates);
            if(unchanged != null) {
                return unchanged;
            }
            Map<VariableWriteStatus.Type, List<VariableWriteStatus>> statusesByType = branchUpdates.stream()
                    .flatMap(branchUpdate -> branchUpdate.updatedWrites.values().stream())
                    .collect(Collectors.groupingBy(status -> status.getType()));
            statusesByType.values().forEach(Collections::sort);
            return getNewStatus(event, statusesByType);
        }

        private VariableWriteStatus testAndGetUnchanged(AccessEvent event, List<WriteUpdates> writeUpdates) {
            for(WriteUpdates currentWriteUpdates: writeUpdates) {
                if(currentWriteUpdates.unchanged.containsKey(event)) {
                    if(currentWriteUpdates.unchanged.get(event).getType() != Type.NEW) {
                        return currentWriteUpdates.unchanged.get(event);
                    }
                }
            }
            return null;
        }

        private VariableWriteStatus getNewStatus(AccessEvent event, Map<VariableWriteStatus.Type, List<VariableWriteStatus>> statusesByType) {
            if(statusesByType.getOrDefault(Type.OVERWRITTEN, new ArrayList<>()).size() == branches.size()) {
                return statusesByType.get(Type.OVERWRITTEN).get(0);
            } else if(statusesByType.getOrDefault(Type.USED, new ArrayList<>()).size() >= 1) {
                return statusesByType.get(Type.USED).get(0);
            } else {
                return existingStatus.get(event);
            }            
        }

        @Override
        void handleBranchOpen(ControlEvent event) {
            Branch branch = new Branch(variable, eventIndex, new WriteStatus(existingStatus, newStatus));
            branch.run();
            eventIndex = branch.eventIndex;
            branches.add(branch);
        }

        @Override
        boolean isDefault() {
            return false;
        }
    }

    static class Branch extends VariableAnalysis {
        boolean stop = false;

        Branch(AnalyzedVariable variable, int eventIndex, WriteStatus existingStatus) {
            super(variable, eventIndex, existingStatus);
        }

        @Override
        void run() {
            while(! stop) {
                handleNextEvent();
            }
        }

        @Override
        void handleBranchClose(ControlEvent event) {
            stop = true;
        }

        @Override
        boolean isDefault() {
            return false;
        }
    }
}
