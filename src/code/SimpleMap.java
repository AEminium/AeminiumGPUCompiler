package code;

import aeminium.gpu.lists.DoubleList;
import aeminium.gpu.lists.PList;
import aeminium.gpu.operations.Lambda;

public class SimpleMap {
	public static void main(String[] args) {
		DoubleList in = new DoubleList();
		in.add(1.0);
		in.add(2.0);
		in.add(5.0);
		PList<Float> out = in.map(new Lambda<Double, Float>() {

			@Override
			public Float call(Double input) {
				return (float) Math.sin(input);
			}
			
		});
		
		for (int i = 0; i < out.size(); i++) {
			System.out.println(i + ": " + out.get(i));
		}
		
	}
}
