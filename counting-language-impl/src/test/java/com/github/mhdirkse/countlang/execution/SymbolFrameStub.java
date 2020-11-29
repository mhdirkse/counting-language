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

class SymbolFrameStub implements SymbolFrame<DummyValue> {
    static final String SYMBOL = "x";
    private final boolean hasSymbol;
    private final StackFrameAccess access;

    private int seq;

    SymbolFrameStub(final StackFrameAccess access, final boolean hasSymbol) {
        this.access = access;
        this.hasSymbol = hasSymbol;
    }

    @Override
    public boolean has(String name) {
        if(!name.equals(SYMBOL)) {
            throw new IllegalArgumentException("Only symbol " + SYMBOL + " supported");
        }
        return hasSymbol;
    }

    @Override
    public DummyValue read(String name, int line, int column) {
        throw new IllegalStateException("SymbolFrameStub cannot be read");
    }

    @Override
    public void write(String name, DummyValue value, int line, int column) {
        throw new IllegalStateException("SymbolFrameStub cannot be written");
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    void setSeq(final int seq) {
        this.seq = seq;
    }

    int getSeq() {
        return seq;
    }

    @Override
    public void onSwitchOpened() {
    }

    @Override
    public void onSwitchClosed() {
    }

    @Override
    public void onBranchOpened() {
    }

    @Override
    public void onBranchClosed() {
    }
}
