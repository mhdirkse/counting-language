package com.github.mhdirkse.countlang.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.velocity.VelocityContext;

import com.github.mhdirkse.codegen.compiletime.ClassModel;
import com.github.mhdirkse.codegen.compiletime.ClassModelList;
import com.github.mhdirkse.codegen.compiletime.Input;
import com.github.mhdirkse.codegen.compiletime.Output;
import com.github.mhdirkse.codegen.compiletime.TypeHierarchy;

public class CodegenProgramTest implements Runnable {
    private static final String ACCEPTOR = "com.github.mhdirkse.countlang.generator.test.input.Acceptor";
    private static final String LISTENER = "com.github.mhdirkse.countlang.generator.test.VisitorListener";
    private static final String VISITOR_TO_LISTENER = "com.github.mhdirkse.countlang.generator.test.VisitorToListener";
    private static final String ENTER = "enter";
    private static final String EXIT = "exit";

    @Input("com.github.mhdirkse.countlang.generator.test.input.Visitor")
    public ClassModel source;

    @Output("interfaceTemplate")
    public VelocityContext listener;

    @Output("visitorToListenerTemplate")
    public VelocityContext visitorToListener;

    @TypeHierarchy("com.github.mhdirkse.countlang.generator.test.input.Acceptor")
    public ClassModelList acceptorHierarchy;

    @TypeHierarchy(
            value = "com.github.mhdirkse.countlang.generator.test.input.Acceptor",
            filterIsA = "com.github.mhdirkse.countlang.generator.test.input.Composite")
    public ClassModelList compositeAcceptors;

    Set<String> getClassNames(final List<ClassModel> classModels) {
        return classModels.stream().map(ClassModel::getFullName).collect(Collectors.toSet());
    }

    Set<String> getAtomicAcceptors() {
        Set<String> atomicAcceptors = new HashSet<>(getClassNames(acceptorHierarchy));
        Set<String> compositeAcceptorNames = getClassNames(compositeAcceptors);
        atomicAcceptors.removeAll(compositeAcceptorNames);
        return atomicAcceptors;
    }

    @Override
    public void run() {
        VisitorClassModel visitorToListenerModel = new VisitorClassModel(source);
        visitorToListenerModel.setFullName(VISITOR_TO_LISTENER);
        visitorToListenerModel.extendMethods(getAtomicAcceptors(), ENTER, EXIT);
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
