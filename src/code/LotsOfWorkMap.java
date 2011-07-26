package code;

import aeminium.gpu.collections.lists.DoubleList;
import aeminium.gpu.collections.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;

public class LotsOfWorkMap {
	private final static int ARRAY_SIZE = 1000000;

	public static void main(String[] args) {
		DoubleList input = new DoubleList();
		
		for (int i=0; i<ARRAY_SIZE ; i++) {
			input.add(new Double(i));
		}
		
		PList<Double> output = input.map(new LambdaMapper<Double,Double>() {
		
			public Double map(Double o) {
				double kth = -2 * o * Math.PI / ARRAY_SIZE;
				double c = Math.cos(kth) + 2*Math.sin(kth);
	            return Math.acos(c) * Math.log(kth) - Math.pow(ARRAY_SIZE, c);
			}
			
		});
		System.out.println(output.get(0));
	}
}
