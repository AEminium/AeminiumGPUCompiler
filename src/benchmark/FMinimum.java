package benchmark;

import aeminium.gpu.lists.PList;
import aeminium.gpu.lists.lazyness.Range;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducer;


/*
 * Calculates the minimimum of the polynomial function 10x^6 + x^5 + 2x^4 + 3x^3 + (2/5)x^2 + pi.x
 * between -1 and 1 with a resolution of 10M points.
 */

public class FMinimum {
public static final double RESOLUTION = 10000000.0;
	
	public static void main(String[] args) {
		
		long tmp = System.nanoTime();
		double v = cpuIterative();
		tmp = System.nanoTime() - tmp;
		System.out.println("CPU minimum is: " + v + " and took(ns) " + tmp);
		
		tmp = System.nanoTime();
		v = gpuMapReduce();
		tmp = System.nanoTime() - tmp;
		
		System.out.println("GPU minimum is: " + v + " and took(ns) " + tmp);
	}

	
	private static double cpuIterative(){
		double min = Double.MAX_VALUE;
		
		for(int i=0;i<RESOLUTION;i+=1) {
			double x = 2*i/(double)(RESOLUTION) - 1;
			double v = 10 * Math.pow(x, 6) + Math.pow(x, 5)  + 2 * Math.pow(x, 4) + 3 * x * x * x + 2/5*x*x +Math.PI * x;
			min = Math.min(min,v);
		}
		return min;
		
		
	}

	private static double gpuMapReduce() {
		PList<Integer> li = new Range((int)RESOLUTION);
		
		PList<Double> li2 = li.map(new LambdaMapper<Integer, Double>() {

			@Override
			public Double map(Integer input) {
				double x = 2*input/(double)(RESOLUTION) - 1;
				return 10 * Math.pow(x, 6) + Math.pow(x, 5)  + 2 * Math.pow(x, 4) + 3 * x * x * x + 2/5*x*x +Math.PI * x;
			}
			
		});
		
		return li2.reduce(new LambdaReducer<Double>(){

			@Override
			public Double combine(Double input, Double other) {
				return Math.min(input,other);
			}
			
			@Override
			public Double getSeed() {
				return Double.MAX_VALUE;
			}
			
		});
	}
		
}
