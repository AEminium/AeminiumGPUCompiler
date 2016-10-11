package benchmark;

import aeminium.gpu.collections.lazyness.RandomList;
import aeminium.gpu.collections.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;

public class PICalc {
	public static void main(String[] args) {
		int N = 1034;

		PList<Float> input = new RandomList(N, 123);
		input = input.map(new LambdaMapper<Float, Float>() {

			@Override
			public Float map(Float input) {
				return input * 2;
			}

		});

		for (int i=0; i<20; i++)
			System.out.println("E(" + i + ") = " + input.get(i));
	}
}
