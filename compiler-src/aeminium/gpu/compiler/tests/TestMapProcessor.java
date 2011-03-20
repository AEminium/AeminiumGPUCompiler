package aeminium.gpu.compiler.tests;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import spoon.contrib.tester.ProcessorTestHelper;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtClass;
import spoon.support.builder.CtFile;
import aeminium.gpu.compiler.processing.MapLambdaProcessor;
import aeminium.gpu.compiler.tests.utils.CtFile4Map;
import aeminium.gpu.compiler.tests.utils.MapTestHelper;

@SuppressWarnings("rawtypes")
public class TestMapProcessor extends TestCase {
	
	protected Processor p;
	
	@Before
    public void setUp() {
        p = new MapLambdaProcessor();
    }
	
	@Test
    public void testStaticReturn() throws Exception {
        String java = "return 1;";
        CtFile ctsrc = new CtFile4Map(java);
        CtClass res = (CtClass) ProcessorTestHelper.transform(p, ctsrc);
        
        CtClass lambda = MapTestHelper.getLambda(res);
        assertEquals(2, lambda.getMethods().size());
        
        String cl = MapTestHelper.getOpenCL(lambda);
        assertTrue(cl.contains("return 1;"));
        
    }
	
	@Test
    public void testPlusReturn() throws Exception {
        String java = "return 1 + input;";
        CtFile ctsrc = new CtFile4Map(java);
        CtClass res = (CtClass) ProcessorTestHelper.transform(p, ctsrc);
        
        CtClass lambda = MapTestHelper.getLambda(res);
        assertEquals(2, lambda.getMethods().size());
        
        String cl = MapTestHelper.getOpenCL(lambda);
        assertTrue(cl.contains("return 1 + input;"));
        
    }
	
	
}
