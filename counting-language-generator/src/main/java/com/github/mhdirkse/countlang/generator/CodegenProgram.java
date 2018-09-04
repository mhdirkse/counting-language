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
import com.github.mhdirkse.codegen.compiletime.VelocityContexts;

public class CodegenProgram implements Runnable {
    private static final String DELEGATOR = "com.github.mhdirkse.countlang.lang.parsing.CountlangListenerDelegator";
    private static final String HANDLER = "com.github.mhdirkse.countlang.lang.parsing.CountlangListenerHandler";
    private static final String ABSTRACT_HANDLER = "com.github.mhdirkse.countlang.lang.parsing.AbstractCountlangListenerHandler";

    private static final String AST_ACCEPTOR = "com.github.mhdirkse.countlang.ast.AstNode";
    private static final String AST_LISTENER = "com.github.mhdirkse.countlang.ast.AstListener";
    private static final String AST_ABSTRACT_LISTENER = "com.github.mhdirkse.countlang.ast.AbstractAstListener";
    private static final String AST_VISITOR_TO_LISTENER = "com.github.mhdirkse.countlang.ast.AstVisitorToListener";
    private static final String AST_LISTENER_DELEGATOR = "com.github.mhdirkse.countlang.ast.AstListenerDelegator";
    private static final String AST_LISTENER_HANDLER = "com.github.mhdirkse.countlang.ast.AstListenerHandler";
    private static final String AST_LISTENER_ABSTRACT_HANDLER = "com.github.mhdirkse.countlang.ast.AstListenerAbstractHandler";
    private static final String ENTER = "enter";
    private static final String EXIT = "exit";

    @Input("com.github.mhdirkse.countlang.lang.CountlangListener")
    public ClassModel source;

    @Output("delegatorClassTemplate")
    public VelocityContext chain;

    @Output("interfaceTemplate")
    public VelocityContext chainHandler;

    @Output("abstractImplementationTemplateReturningBoolean")
    public VelocityContext chainAbstractHandler;

    @Input("com.github.mhdirkse.countlang.ast.Visitor")
    public ClassModel astSource;

    @Output("interfaceTemplate")
    public VelocityContext astListener;

    @Output("visitorToListenerTemplate")
    public VelocityContext astVisitorToListener;

    @Output("abstractImplementationTemplate")
    public VelocityContext astAbstractListener;

    @TypeHierarchy("com.github.mhdirkse.countlang.ast.AstNode")
    public ClassModelList astHierarchy;

    @TypeHierarchy(value = "com.github.mhdirkse.countlang.ast.AstNode",
            filterIsA = "com.github.mhdirkse.countlang.ast.CompositeNode")
    public ClassModelList astComposites;

    @Output("delegatorClassTemplate")
    public VelocityContext astChain;

    @Output("interfaceTemplate")
    public VelocityContext astChainHandler;

    @Output("abstractImplementationTemplateReturningBoolean")
    public VelocityContext astChainAbstractHandler;

    @Override
    public void run() {
        language();
        ast();
    }

    private void language() {
        VelocityContexts.populateChainContext(
                source,
                DELEGATOR,
                HANDLER,
                chain);
        VelocityContexts.populateChainHandlerContext(
                source,
                DELEGATOR,
                HANDLER,
                chainHandler);
        VelocityContexts.populateChainAbstractHandlerContext(
                source,
                HANDLER,
                ABSTRACT_HANDLER,
                chainAbstractHandler);
    }

    public void ast() {
        VisitorClassModel visitorToListenerModel = new VisitorClassModel(astSource);
        visitorToListenerModel.setFullName(AST_VISITOR_TO_LISTENER);
        visitorToListenerModel.extendMethods(getAtomicAcceptors(), ENTER, EXIT);
        ClassModel listenerModel = new ClassModel();
        listenerModel.setFullName(AST_LISTENER);
        listenerModel.setMethods(visitorToListenerModel.getListenerMethods());
        ClassModel abstractListenerModel = new ClassModel(listenerModel);
        abstractListenerModel.setFullName(AST_ABSTRACT_LISTENER);
        astVisitorToListener.put("acceptor", AST_ACCEPTOR);
        astVisitorToListener.put("source", astSource);
        astVisitorToListener.put("target", visitorToListenerModel);
        astVisitorToListener.put("listener", listenerModel);
        astListener.put("target", listenerModel);
        astAbstractListener.put("source", listenerModel);
        astAbstractListener.put("target", abstractListenerModel);
        astDelegation(listenerModel);
    }

    public void astDelegation(final ClassModel source) {
        VelocityContexts.populateChainContext(
                source,
                AST_LISTENER_DELEGATOR,
                AST_LISTENER_HANDLER,
                astChain);
        VelocityContexts.populateChainHandlerContext(
                source,
                AST_LISTENER_DELEGATOR,
                AST_LISTENER_HANDLER,
                astChainHandler);
        VelocityContexts.populateChainAbstractHandlerContext(
                source,
                AST_LISTENER_HANDLER,
                AST_LISTENER_ABSTRACT_HANDLER,
                astChainAbstractHandler);
    }

    Set<String> getClassNames(final List<ClassModel> classModels) {
        return classModels.stream().map(ClassModel::getFullName).collect(Collectors.toSet());
    }

    Set<String> getAtomicAcceptors() {
        Set<String> atomicAcceptors = new HashSet<>(getClassNames(astHierarchy));
        Set<String> compositeAcceptorNames = getClassNames(astComposites);
        atomicAcceptors.removeAll(compositeAcceptorNames);
        return atomicAcceptors;
    }
}
