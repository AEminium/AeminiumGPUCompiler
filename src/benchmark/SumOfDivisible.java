package benchmark;

import aeminium.gpu.collections.lazyness.Range;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducerWithSeed;


// This class computes the sum of the 1M first natural numbers that are divisible by 7.

public class SumOfDivisible {

	public static int FIRST_NATURAL_NUMBERS = 5000000;
	
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
		for(int i=1;i<=FIRST_NATURAL_NUMBERS;i+=1) {
			if (i % 7 == 0) {
				sum += i;
			}
		}
		return sum;
	}

	private static long gpuMapReduce() {
		return new Range(FIRST_NATURAL_NUMBERS).map(new LambdaMapper<Integer, Long>() {

			@Override
			public Long map(Integer input) {
				return (input+1) % 7 == 0 ? (long)input+1 : 0L;
			}
			
		}).reduce(new LambdaReducerWithSeed<Long>(){

			@Override
			public Long combine(Long input, Long other) {
				return input + other;
			}
			
			@Override
			public Long getSeed() {
				return 0L;
			}
			
		});
		
	}
	
}
