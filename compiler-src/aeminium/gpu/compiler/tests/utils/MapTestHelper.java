package aeminium.gpu.compiler.tests.utils;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class MapTestHelper {
	
	
	@SuppressWarnings("rawtypes")
	public static CtClass getLambda(CtClass c) {
		CtMethod m = (CtMethod) c.getAllMethods().toArray()[0];
        CtBlock b = m.getBody();
        CtInvocation inv = (CtInvocation) b.getStatements().get(1);
        CtNewClass nc = (CtNewClass) inv.getArguments().get(0);
        return nc.getAnonymousClass();
	}
	
	@SuppressWarnings("rawtypes")
	public static String getOpenCL(CtClass c) {
		CtMethod m = (CtMethod) c.getMethods().toArray()[1];
		CtBlock b = m.getBody();
		CtReturn r = (CtReturn) b.getStatements().get(0);
		CtLiteral l = (CtLiteral) r.getReturnedExpression();
		return (String) l.getValue();                             
	}
}
