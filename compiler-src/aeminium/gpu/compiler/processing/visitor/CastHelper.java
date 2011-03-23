package aeminium.gpu.compiler.processing.visitor;

import spoon.reflect.reference.CtTypeReference;
import aeminium.gpu.compiler.processing.opencl.CLType;

public class CastHelper {
	
	private static String makeCast(String t) {
		if (t == null) {
			return "";
		} else {
			return "(" + t + ") ";
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static String getReturnType(CLType ct, CtTypeReference jt) {
		switch (ct) {
		case INT:
			// TODO - int function
			break;
		case FLOAT_OR_DOUBLE:
			if (!jt.getSimpleName().equals("double") && !jt.getSimpleName().equals("float")) {
				return "double";
			}
			break;
		default:
			break;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static String getReturnTypeCast(CLType ct, CtTypeReference jt) {
		return makeCast(getReturnType(ct, jt));
	}
	
}
