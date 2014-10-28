package org.mockito.internal.rules;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;

import static org.junit.Assert.*;

public class JUnitRuleTest {

    private JUnitRule jUnitRule;
    private InjectTestCase injectTestCase;

    @Before
    public void setUp() throws Exception {
        injectTestCase = new InjectTestCase();
        jUnitRule = new JUnitRule();
    }

    @Test
    public void testInject() throws Throwable {
        jUnitRule.apply(new DummyStatement(), injectTestCase).evaluate();
        assertNotNull("@Mock mock object created", injectTestCase.getInjected());
        assertNotNull("@InjectMocks object created", injectTestCase.getInjectInto());
        assertNotNull("Mock injected into the object", injectTestCase.getInjectInto().getInjected());
    }

    @Test
    public void testThrowAnException() throws Throwable {
        try {
            jUnitRule.apply(new ExceptionStatement(), injectTestCase).evaluate();
            fail("Should throw exception");
        } catch (RuntimeException e) {
            assertEquals("Correct message", "Statement exception", e.getMessage());
        }
    }

    @Test
    public void testMockitoValidation() throws Throwable {
        try {
            jUnitRule.apply(new UnfinishedStubbingStatement(), injectTestCase).evaluate();
            fail("Should detect invalid Mockito usage");
        } catch (UnfinishedStubbingException e) {
        }
    }

    private static class DummyStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {
        }
    }

    private static class ExceptionStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {
            throw new RuntimeException("Statement exception");
        }
    }

    private static class UnfinishedStubbingStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {
            InjectTestCase injectTestCase = new InjectTestCase();
            MockitoAnnotations.initMocks(injectTestCase);
            injectTestCase.unfinishedStubbingThrowsException();
        }
    }

    public static class InjectTestCase {

        @Mock
        private Injected injected;

        @InjectMocks
        private InjectInto injectInto;

        @Test
        public void dummy() throws Exception {
        }

        public void unfinishedStubbingThrowsException() throws Exception {
            Mockito.when(injected.stringMethod());
        }

        public Injected getInjected() {
            return injected;
        }

        public InjectInto getInjectInto() {
            return injectInto;
        }

        public static class Injected {
            public String stringMethod() {
                return "string";
            }
        }

        public static class InjectInto {
            private Injected injected;

            public Injected getInjected() {
                return injected;
            }
        }

    }
}