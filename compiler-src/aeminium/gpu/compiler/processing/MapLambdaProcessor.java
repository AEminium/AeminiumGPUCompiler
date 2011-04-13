package aeminium.gpu.compiler.processing;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.template.Substitution;
import spoon.template.Template;
import aeminium.gpu.compiler.template.MapLambdaTemplate;
import aeminium.gpu.devices.DefaultDeviceFactory;
import aeminium.gpu.devices.GPUDevice;
import aeminium.gpu.operations.generator.MapCodeGen;

public class MapLambdaProcessor<T>  extends AbstractLambdaProcessor<T>{

	
	@Override
	public void process(CtMethod<T> target) {
		if (target.getSimpleName().equals("map")) {
			checkAndReplaceMethodBody(target);
			checkConsistency(target);
		}
	}
	
	private void checkAndReplaceMethodBody(CtMethod<T> target) {
		parseParameters(target);
		CtBlock<T> body = target.getBody();
		checkAndGenerateExpr(body, params);
		
		if (canSubstitute) {
			String clString = clCode.toString();
			String id = getOpId(target);
			preCompile(target, clString, id);
			Template t = new MapLambdaTemplate(clString, id, params);
			Substitution.insertAllMethods(target.getParent(CtClass.class), t);
		}
	}
	
	protected void preCompile(CtMethod<T> target, String clString, String id) {
		String inputType = target.getParameters().get(0).getType().getQualifiedName();
		String outputType = target.getType().getQualifiedName();
		MapCodeGen g = new MapCodeGen(inputType, outputType, clString, params, id);
		
		GPUDevice gpu = (new DefaultDeviceFactory()).getDevice();
		// This relies in JavaCL's builtin binary caching.
		gpu.compile(g.getMapKernelSource());
		
	}

}
