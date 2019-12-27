/**
 * This package parses a program written in the counting-language language. Class
 * {@link ParseEntryPoint} is the entry point. It calls ANTLR to parse the language.
 * To ANTLR it provides a listener implemented in the generated class
 * {@link CountlangListenerDelegator}. That class delegates to a chain of handlers
 * generated by the codegenPlugin plugin. These handlers have the same
 * methods as the ANTLR 4 generated class {@link com.github.mhdirkse.countlang.lang.CountlangListener}.
 * {@link CountlangListenerDelegator} calls the registered handlers from first to last until
 * a handler returns true. If no handler does so, an exception is thrown.
 * {@link ParseEntryPoint} starts by providing a {@link RootHandler} and then a {@link IgnoredMethodsHandler}.
 * The first of these is collects the AST, while the last ensures that irrelevant ANTLR 4 calls are handled.
 * <p>
 * {@link RootHandler} only has to deal with the enterProg and exitProg methods. On enterProg,
 * a {@link ProgHandler} is added that handles all language elements inside the prog. This is
 * done by adding and removing handlers dynamically. None of these can handle the exitProg event,
 * so when that one is called on {@link RootHandler}, parsing is done. The handler after the
 * {@link RootHandler}, which is {@link ProgHandler}, is responsible for building the AST
 * node {@link com.github.mhdirkse.countlang.ast.Program}.
 * <p>
 * Both a AST program node and an AST function definition contain a group of statements.
 * Therefore, {@link ProgHandler} has a parent class {@link AbstractStatementGroupHandler}.
 * This class assumes that ANTLR 4 is processing the contents of a statement group and handles
 * all enter and exit statements for all statement types. The statement types can be found in
 * the grammar, Countlang.g4. We have the assignment statement, the print statement, the
 * function definition statement and the return statement, each having an enter and an exit event.
 * These eight events are the methods implemented in {@link AbstractStatementGroupHandler}.
 * This results in AST statement nodes, which it passes to abstract method
 * {@link AbstractStatementGroupHandler#addStatement}. This abstract method is implemented by
 * {@link ProgHandler#addStatement}, allowing
 * {@link ProgHandler} to build the AST program node.
 * <p>
 * Any other ANTLR 4 call is either handled by another handler related to some inner language structure,
 * or falls through to the {@link RootHandler}. When the latter happens, parsing is done.
 * The {@link RootHandler} accesses the previous handler to fetch the AST program node and then
 * removes all preceeding handlers.
 * <p>
 * Similar algorithms are used to build smaller structures of the language like statements and
 * expressions. When a handler H1 receives an ANTLR 4 event that starts an inner structure, it
 * adds a new handler H2. Handler H2 is responsible for producing the AST node of that inner
 * structure. Handler H2 should not process the ANTLR 4 event that closes the inner structure. That
 * call falls through to handler H1. Handler H1 then fetches the AST node of the inner structure from H2 and
 * removes all preceeding handlers including H2. H1 then includes the AST node as a child of some 
 * parent AST node, to be fetched eventually by a next handler H0.
 * <p>
 * Please study the type hierarchy of {@link AbstractCountlangListenerHandler}. This hierarchy makes
 * clear how event handling common to building different types of AST nodes is refactored into superclasses.
 *
 * @author Martijn Dirkse
 */
package com.github.mhdirkse.countlang.lang.parsing;