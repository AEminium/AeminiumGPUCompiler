package code;

import aeminium.gpu.collections.factories.CollectionFactory;
import aeminium.gpu.collections.lazyness.RandomList;
import aeminium.gpu.collections.lists.PList;
import aeminium.gpu.collections.matrices.PMatrix;
import aeminium.gpu.operations.functions.LambdaReducer;

public class PiCalc {
	public static void main(String[] args) {
		RandomList rl = new RandomList(1000, 123);
		PMatrix<Float> m = CollectionFactory.matrixfromPList(rl, 500, 2);
		PList<Float> pair = m.reduceLines(new LambdaReducer<Float>() {

			@Override
			public Float getSeed() {
				return (float)0;
			}

			@Override
			public Float combine(Float input, Float other) {
				return (Math.pow(input, 2) + Math.pow(other, 2) < 1) ? 0f : 1f;
			}
		});

		Float f = pair.reduce(new LambdaReducer<Float>() {
			@Override
			public Float combine(Float input, Float output) {
				return input + output;
			}

			@Override
			public Float getSeed() {
				return (float)0;
			}
		});

		System.out.println("F: " + f);

	}
}
