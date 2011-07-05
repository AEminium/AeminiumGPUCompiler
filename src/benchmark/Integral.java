package benchmark;

import aeminium.gpu.lists.PList;
import aeminium.gpu.lists.lazyness.Range;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducer;

public class Integral {
public static int RESOLUTION = 500;
	
	public static void main(String[] args) {
		
		long tmp = System.nanoTime();
		double v = cpuIterative();
		tmp = System.nanoTime() - tmp;
		System.out.println("CPU integral is: " + v + " and took(ns) " + tmp);
		
		tmp = System.nanoTime();
		v = gpuMapReduce();
		tmp = System.nanoTime() - tmp;
		
		System.out.println("GPU integral is: " + v + " and took(ns) " + tmp);
	}

	
	private static double f(double x) {
		return Math.pow(Math.E, Math.sin(x)); 
	}
	
	private static double cpuIterative() {
		double sum = 0;
		for(int i=0; i<RESOLUTION;i++) {
			double min = i /RESOLUTION;
			double max = i+1 /RESOLUTION;
			double area = (f(min) + f(max)) /(RESOLUTION*2);
			sum += area;
		}
		return sum;
	}

	private static Double gpuMapReduce() {
		
		PList<Integer> integers = new Range(RESOLUTION);
		
		Double o = integers.map(new LambdaMapper<Integer, Double>() {

			@Override
			public Double map(Integer i) {
				double min = i /RESOLUTION;
				double max = i+1/RESOLUTION;
				double area = (f(min) + f(max)) /(RESOLUTION*2);
				return area;
			}
			
		}).evaluate().reduce(new LambdaReducer<Double>() {

			@Override
			public Double combine(Double a, Double b) {
				return a+b;
			}

			@Override
			public Double getSeed() {
				return 0.0;
			}
			
		});
		return o;
	}
		
}
