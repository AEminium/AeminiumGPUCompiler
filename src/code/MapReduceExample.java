package code;

import aeminium.gpu.collections.lists.IntList;
import aeminium.gpu.collections.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducerWithSeed;

public class MapReduceExample {
	public static void main(String[] args) {
		int N = 512;
		
		PList<Integer> input = new IntList();
		for (int i = 0; i < N; i++) {
			input.add(i);
		}
		
		input = input.map(new LambdaMapper<Integer, Integer>() {

			@Override
			public Integer map(Integer input) {
				return input + 1;
			}
			
		});
		input.get(0);
		int sum = input.reduce(new LambdaReducerWithSeed<Integer>(){

			@Override
			public Integer combine(Integer input, Integer other) {
				return input + other;
			}
			
			@Override
			public Integer getSeed() {
				return 0;
			}
			
		});
		System.out.println("The sum of the first " + N + " numbers is " + sum);
		
		sum = input.reduce(new LambdaReducerWithSeed<Integer>(){

			@Override
			public Integer combine(Integer input, Integer other) {
				return Math.min(input,other);
			}
			
			@Override
			public Integer getSeed() {
				return Integer.MAX_VALUE;
			}
			
		});
		
		System.out.println("The min of the first " + N + " numbers is " + sum);
		
		
	}
}
