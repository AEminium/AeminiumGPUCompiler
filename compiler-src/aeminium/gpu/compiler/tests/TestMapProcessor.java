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
	
	
	public void compareJavaWithOpenCL(String java, String opencl) throws Exception {
		CtFile ctsrc = new CtFile4Map(java);
        CtClass res = (CtClass) ProcessorTestHelper.transform(p, ctsrc);
        
        CtClass lambda = MapTestHelper.getLambda(res);
        assertEquals(2, lambda.getMethods().size());
        
        String cl = MapTestHelper.getOpenCL(lambda);
        assertTrue(cl.contains(opencl));
	}
	
	@Test
    public void testStaticReturn() throws Exception {
        compareJavaWithOpenCL("return 1;", "return 1;");
    }
	
	@Test
    public void testPlusReturn() throws Exception {
		compareJavaWithOpenCL("return 1 + input;", "return 1 + input;");
    }
	
	@Test
    public void testEvenReturn() throws Exception {
		compareJavaWithOpenCL("return (input % 2 == 0) ? 1 : 2;", "return (input % 2) == 0 ? 1 : 2;");
    }
	
	@Test
    public void testIfStmt() throws Exception {
		compareJavaWithOpenCL(
				"if (input > 10) { int k = input-10; return k; } else return 1;", 
				"if (input > 10) {\nint k = input - 10;\nreturn k;\n} else\nreturn 1;");
    }
	
	@Test
    public void testForLoop() throws Exception {
		compareJavaWithOpenCL(
				"int t=input; for(int i=0;i<10;i++) { t += i; } return t;", 
				"int t = input;\nfor (int i = 0 ; i < 10 ; i++) {\nt += i;\n}\nreturn t;");
    }
	
	@Test
	public void testWhileLoop() throws Exception {
		compareJavaWithOpenCL(
				"int t=input; while(t < 10) { t *= 2; } return t;", 
				"int t = input;\nwhile (t < 10) {\nt *= 2;\n}\nreturn t;");
    }
	
	
}
