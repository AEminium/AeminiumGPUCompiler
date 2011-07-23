package aeminium.gpu.compiler.processing.opencl;

import java.util.HashMap;

public class BoxedTypes {
	
	static String[] allowed = new String[] { 
			"java.lang.Integer",
			"java.lang.Float",
			"java.lang.Double",
			"java.lang.Character",
			"java.lang.Long",
		};
	
	public static HashMap<String, String> typeVariables = new HashMap<String,String>();
	
	static {
		typeVariables.put("java.lang.Double#MAX_VALUE", new Double(Double.MAX_VALUE).toString());	
	}
	
	public static boolean hasClass(String kl) {
		for (int i = 0; i < allowed.length; i++) {
			if (allowed[i].equals(kl)) {
				return true;
			}
		}
		return false;
	}
}
