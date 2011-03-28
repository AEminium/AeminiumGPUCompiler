package aeminium.gpu.compiler.processing;

import aeminium.gpu.compiler.processing.visitor.OpenCLCodeGeneratorVisitor;
import aeminium.gpu.compiler.template.MapLambdaTemplate;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.template.Substitution;
import spoon.template.Template;

public class MapLambdaProcessor<T>  extends AbstractProcessor<CtMethod<T>>{

	private boolean canSubstitute = false;
	private String clCode = null;
	
	@Override
	public void process(CtMethod<T> target) {
		if (target.getSimpleName().equals("call")) {

			checkAndReplaceMethodBody(target);
			
			if (canSubstitute) {
				// Check for consistency and correct it.
				ModelConsistencyChecker checker = new ModelConsistencyChecker(this.getEnvironment(), true);
				checker.enter(target);
			}
			
		}
	}
	
	private void checkAndReplaceMethodBody(CtMethod<T> target) {
		CtBlock<T> body = target.getBody();
		String input_var = target.getParameters().get(0).getSimpleName();
		checkAndGenerateExpr(body, input_var);
		if (canSubstitute) {
			// TODO: Compile code.
			String clString = clCode.toString();
			Template t = new MapLambdaTemplate(clString);
			Substitution.insertAllMethods(target.getParent(CtClass.class), t);
		}
	}
	
	private void checkAndGenerateExpr(CtElement expr, String input_var) {
		
		OpenCLCodeGeneratorVisitor gen = new OpenCLCodeGeneratorVisitor(getEnvironment(), input_var);
		expr.accept(gen);
		
		if (gen.canBeGenerated()) {
			canSubstitute = true;
			clCode = gen.toString();
			System.out.println(":::: Final Code ::::");
			System.out.println(clCode);
			System.out.println(":: End Final Code ::");
		}
	}

}
