package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.utils.ListComparator;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class CountlangTuple implements Comparable<CountlangTuple>, CountlangComposite {
    private final List<Object> members;

    public CountlangTuple(List<Object> members) {
        this.members = flattenMembers(members);
    }

    private static List<Object> flattenMembers(List<Object> orig) {
        List<Object> result = new ArrayList<>();
        for(Object member: orig) {
            if(member instanceof CountlangTuple) {
                result.addAll(((CountlangTuple) member).getMembers());
            } else {
                result.add(member);
            }
        }
        return result;
    }

    public List<Object> getMembers() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public Object get(int i) {
        return members.get(i);
    }

    @Override
    public int compareTo(CountlangTuple o) {
        return ListComparator.getInstance().compare(this.members, o.members);
    }

    @Override
    public String toString() {
        return "[" + members.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }
}
