package aeminium.gpu.compiler.processing;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.template.Substitution;
import spoon.template.Template;
import aeminium.gpu.compiler.processing.visitor.OpenCLCodeGeneratorVisitor;
import aeminium.gpu.compiler.template.MapLambdaTemplate;
import aeminium.gpu.devices.DefaultDeviceFactory;
import aeminium.gpu.devices.GPUDevice;
import aeminium.gpu.operations.generator.MapCodeGen;

public class MapLambdaProcessor<T>  extends AbstractProcessor<CtMethod<T>>{

	private static int mapCounter = 0;
	
	private boolean canSubstitute = false;
	private String clCode = null;
	
	@Override
	public void process(CtMethod<T> target) {
		if (target.getSimpleName().equals("map")) {

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
			String clString = clCode.toString();
			
			String id = getMapId(target);
			preCompile(target, clString, id);
			Template t = new MapLambdaTemplate(clString, id);
			Substitution.insertAllMethods(target.getParent(CtClass.class), t);
		}
	}

	private String getMapId(CtMethod<T> target) {
		String qName = target.getPosition().getCompilationUnit().getMainType().getQualifiedName();
		qName = qName.replace(".", "_");
		return qName + (mapCounter++);
	}
	
	private void checkAndGenerateExpr(CtElement expr, String input_var) {
		
		OpenCLCodeGeneratorVisitor gen = new OpenCLCodeGeneratorVisitor(getEnvironment(), input_var);
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
	
	private void preCompile(CtMethod<T> target, String clString, String id) {
		String inputType = target.getParameters().get(0).getType().getQualifiedName();
		String outputType = target.getType().getQualifiedName();
		MapCodeGen g = new MapCodeGen(inputType, outputType, clString, id);
		
		GPUDevice gpu = (new DefaultDeviceFactory()).getDevice();
		// This relies in JavaCL's builtin binary caching.
		gpu.compile(g.getMapKernelSource());
		
	}

}
