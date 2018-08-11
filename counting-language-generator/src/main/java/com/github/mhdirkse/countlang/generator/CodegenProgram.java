package com.github.mhdirkse.countlang.generator;

import org.apache.velocity.VelocityContext;

import com.github.mhdirkse.codegen.compiletime.ClassModel;
import com.github.mhdirkse.codegen.compiletime.Input;
import com.github.mhdirkse.codegen.compiletime.Output;
import com.github.mhdirkse.codegen.compiletime.VelocityContexts;

public class CodegenProgram implements Runnable {
    private static final String DELEGATOR = "com.github.mhdirkse.countlang.lang.parsing.CountlangListenerDelegator";
    private static final String HANDLER = "com.github.mhdirkse.countlang.lang.parsing.CountlangListenerHandler";
    private static final String ABSTRACT_HANDLER = "com.github.mhdirkse.countlang.lang.parsing.AbstractCountlangListenerHandler";
    
    @Input("com.github.mhdirkse.countlang.lang.CountlangListener")
    public ClassModel source;

    @Output("delegatorClassTemplate")
    public VelocityContext chain;

    @Output("interfaceTemplate")
    public VelocityContext chainHandler;

    @Output("abstractImplementationTemplateReturningBoolean")
    public VelocityContext chainAbstractHandler;

    @Override
    public void run() {
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
}
