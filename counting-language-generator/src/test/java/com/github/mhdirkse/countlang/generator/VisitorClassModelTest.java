package com.github.mhdirkse.countlang.generator;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.codegen.compiletime.MethodModel;

import org.junit.Assert;

public class VisitorClassModelTest {
    private MethodModel methodModel;
    private MethodModel methodModelWithoutVisit;
    private MethodModel methodModelVisitNotAtStart;
    @Before
    public void setUp() {
        methodModel = new MethodModel();
        methodModel.setName("visitX");
        methodModelWithoutVisit = new MethodModel();
        methodModelWithoutVisit.setName("x");
        methodModelVisitNotAtStart = new MethodModel();
        methodModelVisitNotAtStart.setName("dovisit");
    }

    @Test
    public void whenNameStartsWithVisitThenGetListenerMethodReplacesWithPrefix() {
        Assert.assertEquals("enterX", VisitorClassModel.getListenerMethod(methodModel, "enter").getName());
    }

    @Test
    public void whenNameDoesNotStartWithVisitThenGetListenerMethodPrependsPrefix() {
        Assert.assertEquals("enterX",
                VisitorClassModel.getListenerMethod(methodModelWithoutVisit, "enter").getName());
    }

    @Test
    public void whenNameHasVisitInMiddleThenGetListenerMethodPrependsPrefix() {
        Assert.assertEquals("enterDovisit",
                VisitorClassModel.getListenerMethod(methodModelVisitNotAtStart, "enter").getName());
    }
}
