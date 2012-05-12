package code;

import aeminium.gpu.collections.lists.FloatList;
import aeminium.gpu.collections.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;

public class FFTish {
	private static final int ARRAY_SIZE = 10000000;

	public static void main(String[] args) {
		FloatList input = new FloatList();

		for (int i = 0; i < ARRAY_SIZE; i++) {
			input.add(new Float(i));
		}

		PList<Float> output = input.map(new LambdaMapper<Float, Float>() {

			public Float map(Float o) {
				double kth = -2 * o * Math.PI / ARRAY_SIZE;
				return new Float(Math.cos(kth) + 2 * Math.sin(kth));
			}

		});
		System.out.println(output.get(0));
		System.out.println(output.get(1));
		System.out.println(output.get(ARRAY_SIZE - 1));
	}
}
