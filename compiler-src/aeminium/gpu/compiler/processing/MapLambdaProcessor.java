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
	public void process(CtClass<T> target) {
		if (target.getSuperclass() != null) {
			if (target.getSuperclass().toString().equals("aeminium.gpu.operations.functions.LambdaMapper")) {
				for(CtMethod<?> m : target.getMethods()) {
					if (m.getSimpleName().equals("map")) {
						checkAndReplaceMethodBody(m);
					}
				}
			}
		}
		checkConsistency(target);
	}
	
	private <K> void checkAndReplaceMethodBody(CtMethod<K> target) {
		parseParameters(target);
		CtBlock<K> body = target.getBody();
		clCode = checkAndGenerateExpr(body, params);
		
		if (canSubstitute) {
			String clString = clCode.toString();
			String id = getOpId("map", target);
			preCompile(target, clString, id);
			Template t = new MapLambdaTemplate(clString, id, params);
			Substitution.insertAllMethods(target.getParent(CtClass.class), t);
		}
	}
	
	protected void preCompile(CtMethod<?> target, String clString, String id) {
		String inputType = target.getParameters().get(0).getType().getQualifiedName();
		String outputType = target.getType().getQualifiedName();
		MapCodeGen g = new MapCodeGen(inputType, outputType, clString, params, id);
		
		GPUDevice gpu = (new DefaultDeviceFactory()).getDevice();
		// This relies in JavaCL's builtin binary caching.
		gpu.compile(g.getMapKernelSource());
		
	}

}
