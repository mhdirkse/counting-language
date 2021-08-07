package com.github.mhdirkse.countlang.type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TupleType extends CountlangType {
    private final List<CountlangType> subTypes;

    TupleType(CountlangType head, CountlangType tail) {
        super(CountlangType.Kind.TUPLE, head, tail);
        this.subTypes = createTupleSubTypes(head, tail);
    }

    private static List<CountlangType> createTupleSubTypes(CountlangType head, CountlangType tail) {
        List<CountlangType> result = new ArrayList<>();
        result.add(head);
        if(tail.isTuple()) {
            result.addAll( ((TupleType) tail).getTupleSubTypes());
        } else {
            result.add(tail);
        }
        return result;
    }

    public List<CountlangType> getTupleSubTypes() {
        return subTypes;
    }

    @Override
    public String toString() {
        return "tuple<" + subTypes.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">";
    }
}
