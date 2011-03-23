package aeminium.gpu.compiler.processing.opencl;

public class MathFunction {

	private CLType[] argumentTypes;
	private CLType returnType;
	
	private String openclName;
	
	private String qClass;
	private String methodName;
	
	public MathFunction(String qClass, String methodName, 
			String openclName) {
		this(qClass, methodName, openclName, null, new CLType[] {});
	}
	
	public MathFunction(String qClass, String methodName, 
			String openclName, CLType returnType, CLType[] argumentTypes) {
		super();
		this.argumentTypes = argumentTypes;
		this.openclName = openclName;
		this.returnType = returnType;
		this.qClass = qClass;
		this.methodName = methodName;
	}
	
	public CLType[] getArgumentTypes() {
		return argumentTypes;
	}

	public String getOpenCLName() {
		return openclName;
	}

	public CLType getReturnType() {
		return returnType;
	}

	public String getQualifiedClass() {
		return qClass;
	}

	public String getMethodName() {
		return methodName;
	}
	
	
}
