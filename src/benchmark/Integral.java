package benchmark;

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

	
	private static double cpuIterative(){
		double sum = 0;
		for(int i=0;i<500000;i+=1) {
			double b = Math.pow(Math.E, Math.sin(i / 500000));
			double B = Math.pow(Math.E, Math.sin((i+1) / 500000));				
			sum += ((b+B) / 2 ) * (1/500000);
		}
		return sum;
		
		
	}

	private static double gpuMapReduce() {	
		return new Range(500000).map(new LambdaMapper<Integer, Double>() {

			@Override
			public Double map(Integer input) {
				double b = Math.pow(Math.E, Math.sin(input / 500000));
				double B = Math.pow(Math.E, Math.sin((input+1) / 500000));				
				return ((b+B) / 2 ) * (1/500000);
			}
			
		}).reduce(new LambdaReducer<Double>(){

			@Override
			public Double combine(Double input, Double other) {
				return input + other;
			}
			
			@Override
			public Double getSeed() {
				return 0.0;
			}
			
		});
	}
		
}
