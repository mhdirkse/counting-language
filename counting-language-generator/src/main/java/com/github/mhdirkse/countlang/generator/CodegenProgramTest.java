package com.github.mhdirkse.countlang.generator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import com.github.mhdirkse.codegen.compiletime.ClassModel;
import com.github.mhdirkse.codegen.compiletime.Input;
import com.github.mhdirkse.codegen.compiletime.MethodModel;
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
        ClassModel visitorToListenerModel = new ClassModel(source);
        visitorToListenerModel.setFullName(VISITOR_TO_LISTENER);
        visitorToListenerModel.getMethods().replaceAll(
                m-> getMethodExtra(m));
        ClassModel listenerModel = new ClassModel();
        listenerModel.setFullName(LISTENER);
        listenerModel.setMethods(visitorToListenerModel.getMethods().stream()
                .map(m -> (VisitorMethodModel) m)
                .map(m -> Arrays.asList(
                        m.getVisitMethod(),
                        m.getEnterMethod(),
                        m.getExitMethod()))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(m -> new MethodModel(m))
                .collect(Collectors.toList()));
        visitorToListener.put("acceptor", ACCEPTOR);
        visitorToListener.put("source", source);
        visitorToListener.put("target", visitorToListenerModel);
        visitorToListener.put("listener", listenerModel);
        listener.put("target", listenerModel);
    }

    private VisitorMethodModel getMethodExtra(final MethodModel orig) {
        if(ATOMIC_METHODS.contains(orig.getName())) {
            return getMethodExtraAtomic(orig);
        } else {
            return getMethodExtraComposite(orig);
        }
    }

    private VisitorMethodModel getMethodExtraAtomic(final MethodModel orig) {
        VisitorMethodModel result = new VisitorMethodModel(orig);
        result.setAtomic(true);
        result.setVisitMethod(new MethodModel(orig));
        return result;
    }

    private VisitorMethodModel getMethodExtraComposite(final MethodModel orig) {
        VisitorMethodModel result = new VisitorMethodModel(orig);
        result.setAtomic(false);
        result.setEnterMethod(getListenerMethod(orig, ENTER));
        result.setExitMethod(getListenerMethod(orig, EXIT));
        return result;
    }

    static MethodModel getListenerMethod(final MethodModel m, final String prefix) {
        MethodModel result = new MethodModel(m);
        String withoutVisit = m.getName().replaceFirst("^visit", "");
        result.setName(prefix + StringUtils.capitalize(withoutVisit));
        return result;
    }
}
