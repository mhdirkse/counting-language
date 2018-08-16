package com.github.mhdirkse.countlang.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.velocity.VelocityContext;

import com.github.mhdirkse.codegen.compiletime.ClassModel;
import com.github.mhdirkse.codegen.compiletime.Input;
import com.github.mhdirkse.codegen.compiletime.Output;

public class CodegenProgramTest implements Runnable {
    private static final String ACCEPTOR = "com.github.mhdirkse.countlang.generator.test.input.Acceptor";
    private static final String LISTENER = "com.github.mhdirkse.countlang.generator.test.VisitorListener";
    private static final String VISITOR_TO_LISTENER = "com.github.mhdirkse.countlang.generator.test.VisitorToListener";
    private static final Set<String> ATOMIC_METHODS = new HashSet<>(Arrays.asList("visitAtomicAcceptor"));
    private static final String ENTER = "enter";
    private static final String EXIT = "exit";

    @Input("com.github.mhdirkse.countlang.generator.test.input.Visitor")
    public ClassModel source;

    @Output("interfaceTemplate")
    public VelocityContext listener;

    @Output("visitorToListenerTemplate")
    public VelocityContext visitorToListener;

    @Override
    public void run() {
        VisitorClassModel visitorToListenerModel = new VisitorClassModel(source);
        visitorToListenerModel.setFullName(VISITOR_TO_LISTENER);
        visitorToListenerModel.extendMethods(ATOMIC_METHODS, ENTER, EXIT);
        ClassModel listenerModel = new ClassModel();
        listenerModel.setFullName(LISTENER);
        listenerModel.setMethods(visitorToListenerModel.getListenerMethods());
        visitorToListener.put("acceptor", ACCEPTOR);
        visitorToListener.put("source", source);
        visitorToListener.put("target", visitorToListenerModel);
        visitorToListener.put("listener", listenerModel);
        listener.put("target", listenerModel);
    }
}
