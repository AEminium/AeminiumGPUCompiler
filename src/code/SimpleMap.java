package code;

import aeminium.gpu.lists.DoubleList;
import aeminium.gpu.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;

public class SimpleMap {
	public static void main(String[] args) {
		DoubleList in = new DoubleList();
		in.add(1.0);
		in.add(2.0);
		in.add(5.0);
		PList<Float> out = in.map(new LambdaMapper<Double, Float>() {

			public Float map(Double input_with_other_name) {
				return (float) Math.sin(input_with_other_name);
			}
			
		});
		
		for (int i = 0; i < out.size(); i++) {
			System.out.println(i + ": " + out.get(i));
		}
		
	}
}
