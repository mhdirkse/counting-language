package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * The main task of this class is sorting the events that come out of
 * the {@link com.github.mhdirkse.countlang.execution.SymbolFrameVariableUsage} objects.
 * This is done by capturing each event in an object that can be compared.
 * The class of this object is the inner class
 * {@link com.github.mhdirkse.countlang.execution.SymbolFrameStackVariableUsage.Event}.
 * It is recommended that the comparison is consistent with equals, so 
 * {@link java.lang.Object#equals} and {@link java.lang.Object#hashCode} are also
 * implemented. The actual sorting is done using {@link java.util.TreeSet}. 
 * @author martijn
 *
 */
public class SymbolFrameStackVariableUsage extends SymbolFrameStackImpl<DummyValue, SymbolFrame<DummyValue>>{
    private final List<SymbolFrameVariableUsage> allFrames = new ArrayList<>();
    
    static final class Event implements Comparable<Event> {
        private static final int PRIME = 31;

        final String name;
        final int line;
        final int column;
        
        Event(final String name, final int line, final int column) {
            if(name == null) {
                throw new NullPointerException("name field of SymbolFrameStackVariableUsage.Event should not be null");
            }
            this.name = name;
            this.line = line;
            this.column = column;
        }

        @Override
        public boolean equals(final Object other) {
            if(other == this) {
                return true;
            }
            if(other == null) {
                return false;
            }
            if(! (other instanceof Event)) {
                return false;
            }
            Event ref = (Event) other;
            return (this.line == ref.line)
                    && (this.column == ref.column)
                    && (this.name.equals(ref.name));
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = PRIME * result + line;
            result = PRIME * result + column;
            result = PRIME * result + name.hashCode();
            return result;
        }

        @Override
        public int compareTo(final Event other) {
            int result = compareInt(line, other.line);
            if(result != 0) {
                return result;
            }
            result = compareInt(column, other.column);
            if(result != 0) {
                return result;
            }
            return name.compareTo(other.name);
        }

        private int compareInt(int first, int second) {
            if(first < second) {
                return -1;
            }
            if(first > second) {
                return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d, %s", line, column, name);
        }
    }

    @Override
    SymbolFrame<DummyValue> create(StackFrameAccess access) {
        SymbolFrameVariableUsage frame = new SymbolFrameVariableUsage(access);
        allFrames.add(frame);
        return frame;
    }

    public void listEvents(VariableUsageEventHandler handler) {
        TreeSet<Event> allEvents = new TreeSet<>();
        for(SymbolFrameVariableUsage f: allFrames) {
            f.listEvents((name, line, column) -> allEvents.add(new Event(name, line, column)));
        }
        allEvents.forEach(ev -> handler.variableNotUsed(ev.name, ev.line, ev.column));
    }
}
