package benchmark;

import aeminium.gpu.lists.lazyness.Range;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducer;


// This class computes the sum of the 1M first natural numbers that are divisible by 7.

public class SumOfDivisible {

	public static int FIRST_NATURAL_NUMBERS = 500;
	
	public static void main(String[] args) {
		
		long tmp = System.nanoTime();
		long v = cpuIterative();
		tmp = System.nanoTime() - tmp;
		System.out.println("CPU sum is: " + v + " and took(ns) " + tmp);
		
		tmp = System.nanoTime();
		v = gpuMapReduce();
		tmp = System.nanoTime() - tmp;
		
		System.out.println("GPU sum is: " + v + " and took(ns) " + tmp);
	}

	
	
	private static long cpuIterative() {
		long sum = 0;
		for(int i=1;i<=1000;i+=1) {
			if (i % 7 == 0) {
				sum += i;
			}
		}
		return sum;
	}

	private static long gpuMapReduce() {
		return new Range(1000).map(new LambdaMapper<Integer, Integer>() {

			@Override
			public Integer map(Integer input) {
				if (input+1 % 7 == 0) {
					return input+1;
				}
				else {
					return 0;
				}
			}
			
		}).reduce(new LambdaReducer<Integer>(){

			@Override
			public Integer combine(Integer input, Integer other) {
				return input + other;
			}
			
			@Override
			public Integer getSeed() {
				return 0;
			}
			
		});
		
	}
	
}
