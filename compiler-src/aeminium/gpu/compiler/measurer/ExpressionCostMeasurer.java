package aeminium.gpu.compiler.measurer;

import aeminium.gpu.lists.FloatList;
import aeminium.gpu.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;

public class ExpressionCostMeasurer {
	
	static int[] sizesToTest = new int[16];
	
	static {
		int v = 8;
		for(int i=0; i<16; i++ ) {
			v *= 2;
			sizesToTest[i] = v;
		}
	}
	
	public void measureExprForDifferentSizes(String expr) {
		long v;
		System.out.println("--------");
		System.out.println("E: " + expr);
		for (int i : sizesToTest) {
			v = measureExpr(expr, i);
			System.out.println("i:" + i + " -> " + v);
		}
		
	}
	
	public long measureExpr(final String expr, int n) {
		PList<Float> input = new FloatList();
		for (int i=0; i < n; i++) {
			input.add(i * 1f);
		}
		
		PList<Float> output = input.map(new LambdaMapper<Float,Float>() {

			@Override
			public Float map(Float a) {
				return a;
			}
			
			public String getSource() {
				return "return " + expr + ";";
			}
			
		});
		
		long startTime = System.nanoTime();
		output.get(0); // Force evaluation
		return System.nanoTime() - startTime;
	}
	
	public void measureExprs() {
		ExpressionCostMeasurer cm = new ExpressionCostMeasurer();
		cm.measureExprForDifferentSizes("input + input");
		cm.measureExprForDifferentSizes("sin(input)");
	}
}
