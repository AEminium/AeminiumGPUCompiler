package code;

import aeminium.gpu.GPUDevice;
import aeminium.gpu.lists.FloatList;
import aeminium.gpu.lists.PList;
import aeminium.gpu.operations.Lambda;

public class FFTish {
	private static final int ARRAY_SIZE = 100000;

	public static void main(String[] args) {
		FloatList input = new FloatList();
		
		for (int i=0; i<ARRAY_SIZE ; i++) {
			input.add(new Float(i));
		}
		
		PList<Float> output = input.map(new Lambda<Float,Float>() {
			@Override
			public Float call(Float o) {
				double kth = -2 * o * Math.PI / ARRAY_SIZE;
	            return new Float(Math.cos(kth) + 2*Math.sin(kth));
			}
		
		});
		System.out.println(output.get(0));
	}
}
