package com.github.mhdirkse.countlang.analysis;

import lombok.Getter;

abstract class VariableEvent {
    private final @Getter boolean initial;

    VariableEvent(boolean isInitial) {
        this.initial = isInitial;
    }

    abstract void accept(Visitor v);

    static class Control extends VariableEvent {
        private ControlEvent event;

        Control(ControlEvent event, boolean isInitial) {
            super(isInitial);
            this.event = event;
        }

        @Override
        void accept(Visitor v) {
            v.visitControlEvent(event);
        }
    }

    static class Access extends VariableEvent {
        private AccessEvent event;

        Access(AccessEvent event, boolean isInitial) {
            super(isInitial);
            this.event = event;
        }

        @Override
        void accept(Visitor v) {
            v.visitAccessEvent(event);
        }
    }

    static interface Visitor {
        void visitControlEvent(ControlEvent event);
        void visitAccessEvent(AccessEvent event);
    }
}
