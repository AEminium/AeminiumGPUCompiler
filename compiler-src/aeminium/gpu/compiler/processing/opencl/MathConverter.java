package aeminium.gpu.compiler.processing.opencl;

import java.util.HashMap;

public class MathConverter {
	private static String SEP = "#";
	
	private static HashMap<String,MathFunction> conversion = new HashMap<String,MathFunction>();
	
	private static MathFunction sameFunction(String name, CLType t) {
		return new MathFunction("java.lang.Math", name, name, t, new CLType[] {t});
	}
	private static MathFunction sameFunction2P(String name, CLType t) {
		return new MathFunction("java.lang.Math", name, name, t, new CLType[] {t,t});
	}
	private static MathFunction aliasFunction(String name, String namecl, CLType t) {
		return new MathFunction("java.lang.Math", name, namecl, t, new CLType[] {t});
	}
	
	static {
		register(sameFunction("acos", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("asin", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("atan", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction2P("atan2", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("cbrt", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("ceil", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("cos", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("cosh", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("exp", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("expm1", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("floor", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction2P("hypot", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("log", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("log10", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("log1p", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("max", CLType.INT_OR_FLOAT_OR_DOUBLE));
		register(sameFunction("min", CLType.INT_OR_FLOAT_OR_DOUBLE));
		// TODO: Possible Optimization for Pown
		register(sameFunction2P("pow", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("round", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("rint", CLType.FLOAT_OR_DOUBLE));
		register(aliasFunction("signum", "sign", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("sin", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("sinh", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("sqrt", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("tan", CLType.FLOAT_OR_DOUBLE));
		register(sameFunction("tanh", CLType.FLOAT_OR_DOUBLE));
		register(aliasFunction("toDegrees", "degrees", CLType.FLOAT_OR_DOUBLE));
		register(aliasFunction("toRadians", "radians", CLType.FLOAT_OR_DOUBLE));
	}
	
	private static void register(MathFunction f) {
		conversion.put(makeKey(f), f);
	}
	
	private static String makeKey(MathFunction f) {
		return f.getQualifiedClass() + SEP + f.getMethodName();
	}
	
	private static String makeKey(String qualifiedName, String method) {
		return qualifiedName + "#" + method;
	}
	
	public static boolean hasMethod(String qualifiedName, String method) {
		return conversion.containsKey(makeKey(qualifiedName, method));
	}
	
	public static MathFunction getMathFunction(String qualifiedName, String method) {
		return conversion.get(makeKey(qualifiedName, method));
	}
	
}
