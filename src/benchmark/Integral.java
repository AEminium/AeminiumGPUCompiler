package benchmark;

import aeminium.gpu.lists.PList;
import aeminium.gpu.lists.lazyness.Range;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducer;

public class Integral {
public static double RESOLUTION = 10000000.0;
	
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
		for(int i=0;i<RESOLUTION;i+=1) {
			double b = Math.pow(Math.E, Math.sin(i / RESOLUTION));
			double B = Math.pow(Math.E, Math.sin((i+1) / RESOLUTION));
			sum += ((b+B) / 2 ) * (1/RESOLUTION);
		}
		return sum;
		
		
	}

	private static double gpuMapReduce() {
		PList<Integer> li = new Range((int)RESOLUTION);
		
		PList<Double> li2 = li.map(new LambdaMapper<Integer, Double>() {

			@Override
			public Double map(Integer input) {
				double n = 10000000.0;
				double b = Math.pow(Math.E, Math.sin(input / n));
				double B = Math.pow(Math.E, Math.sin((input+1) / n));
				return ((b+B) / 2 ) * (1/n);
			}
			
		});
		
		li2.get(0);
		
		return li2.reduce(new LambdaReducer<Double>(){

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
