package aeminium.gpu.compiler.measurer;

import aeminium.gpu.lists.FloatList;
import aeminium.gpu.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;

public class ExpressionCostMeasurer {
	
	int[] sizes = new int[] {10, 100, 1000, 10000, 100000, 1000000, 10000000};
	
	public void measureExprForDifferentSizes(String expr) {
		long v;
		System.out.println("--------");
		System.out.println("E: " + expr);
		for (int i : sizes) {
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
		input.clear();
		input = null;
		System.gc();
		
		long startTime = System.nanoTime();
		output.get(0); // Force evaluation
		long totalTime = System.nanoTime() - startTime;
		return totalTime - Configuration.get("time.buffer.Float.from." + n);
	}
	
	public void measureExprs() {
		ExpressionCostMeasurer cm = new ExpressionCostMeasurer();
		cm.measureExprForDifferentSizes("input + input");
		cm.measureExprForDifferentSizes("sin(input)");
	}
}
