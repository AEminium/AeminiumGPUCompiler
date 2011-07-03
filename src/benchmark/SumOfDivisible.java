package benchmark;

import aeminium.gpu.lists.PList;
import aeminium.gpu.lists.lazyness.Range;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducer;


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
		for(int i=0; i<FIRST_NATURAL_NUMBERS;i++) {
			if (i % 7 == 0) sum += i;
		}
		return sum;
	}

	private static Long gpuMapReduce() {
		
		PList<Integer> integers = new Range(FIRST_NATURAL_NUMBERS);
		
		Long o = integers.map(new LambdaMapper<Integer, Long>() {

			@Override
			public Long map(Integer i) {
				return (long) ((i % 7 == 0) ? i : 0);
			}
			
		}).reduce(new LambdaReducer<Long>() {

			@Override
			public Long combine(Long a, Long b) {
				return a+b;
			}

			@Override
			public Long getSeed() {
				return 0L;
			}
			
		});
		return o;
	}
	
}
