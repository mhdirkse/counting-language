package com.github.mhdirkse.countlang.generator.test;

import lombok.Getter;
import lombok.Setter;

public abstract class Acceptor {
    @Getter
    @Setter
    private String name;

    public abstract void accept(Visitor v);
}
