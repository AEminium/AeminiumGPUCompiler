package aeminium.gpu.compiler.processing;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.template.Substitution;
import spoon.template.Template;
import aeminium.gpu.compiler.template.ReduceLambdaTemplate;
import aeminium.gpu.devices.DefaultDeviceFactory;
import aeminium.gpu.devices.GPUDevice;
import aeminium.gpu.operations.generator.ReduceCodeGen;

public class ReduceLambdaProcessor<T>  extends AbstractLambdaProcessor<T>{

	private String seedCode;
	private String inputType;
	private String outputType;
	
	@Override
	public void process(CtClass<T> target) {
		if (target.getSuperclass() != null) {
			if (target.getSuperclass().toString().equals("aeminium.gpu.operations.functions.LambdaReducer")) {
				for(CtMethod<?> m : target.getMethods()) {
					if (m.getSimpleName().equals("combine")) {
						// Extract useful info
						inputType = m.getParameters().get(0).getType().getQualifiedName();
						outputType = m.getType().getQualifiedName();
						parseParameters(m);
						
						clCode = checkMethodBody(m);
					}
					if (m.getSimpleName().equals("getSeed")) {
						seedCode = checkMethodBody(m);
					}
				}
				insertNewMethods(target);
				checkConsistency(target);
			}
		}
	}
	
	private <K> String checkMethodBody(CtMethod<K> target) {
		CtBlock<K> body = target.getBody();
		return checkAndGenerateExpr(body, params);
	}
	
	private void insertNewMethods(CtClass<T> target) {
		if (canSubstitute && clCode != null && seedCode != null) {
			String id = getOpId("reduce", target);
			preCompile(target, clCode, seedCode, id);
			Template t = new ReduceLambdaTemplate(clCode, seedCode, id, params);
			Substitution.insertAllMethods(target, t);
		}
	}
	
	
	protected void preCompile(CtClass<?> target, String clString, String seedString, String id) {
		
		ReduceCodeGen g = new ReduceCodeGen(inputType, outputType, clString, seedString, params, id);
		
		GPUDevice gpu = (new DefaultDeviceFactory()).getDevice();
		// This relies in JavaCL's builtin binary caching.
		gpu.compile(g.getReduceKernelSource());
		
	}

}
