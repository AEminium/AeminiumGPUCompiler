package aeminium.gpu.compiler.processing;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.ModelConsistencyChecker;
import aeminium.gpu.compiler.processing.visitor.OpenCLCodeGeneratorVisitor;

public abstract class AbstractLambdaProcessor<T>  extends AbstractProcessor<CtMethod<T>>{

	protected static int opCounter = 0;
	
	protected boolean canSubstitute = false;
	protected String clCode = null;
	protected String[] params;
	
	
	protected String getOpId(CtMethod<T> target) {
		String qName = target.getPosition().getCompilationUnit().getMainType().getQualifiedName();
		qName = qName.replace(".", "_");
		return qName + (opCounter++);
	}
	
	protected void checkConsistency(CtMethod<T> target) {
		if (canSubstitute) {
			// Check for consistency and correct it.
			ModelConsistencyChecker checker = new ModelConsistencyChecker(this.getEnvironment(), true);
			checker.enter(target);
		}
	}
	
	protected void parseParameters(CtMethod<T> target) {
		params = new String[target.getParameters().size()];
		for (int i=0; i< params.length; i++) {
			params[i] = target.getParameters().get(i).getSimpleName();
		}
	}
	
	
	protected void checkAndGenerateExpr(CtElement expr, String[] input_vars) {
		
		OpenCLCodeGeneratorVisitor gen = new OpenCLCodeGeneratorVisitor(getEnvironment(), input_vars);
		expr.accept(gen);
		
		if (gen.canBeGenerated()) {
			canSubstitute = true;
			clCode = gen.toString();
			if (this.getEnvironment().isVerbose()) {
				System.out.println(":::: Final Code ::::");
				System.out.println(clCode);
				System.out.println(":: End Final Code ::");
			}
		}
	}
	
	protected abstract void preCompile(CtMethod<T> target, String clString, String id);

}
