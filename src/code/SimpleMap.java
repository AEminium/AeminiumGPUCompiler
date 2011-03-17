package code;

import aeminium.gpu.lists.IntList;
import aeminium.gpu.lists.PList;
import aeminium.gpu.operations.Lambda;

public class SimpleMap {
	public static void main(String[] args) {
		IntList in = new IntList();
		in.add(1);
		in.add(2);
		in.add(5);
		PList<Integer> out = in.map(new Lambda<Integer, Integer>() {

			@Override
			public Integer call(Integer input) {
				return input;
			}
			
		});
		
		for (int i = 0; i < out.size(); i++) {
			System.out.println(i + ": " + out.get(i));
		}
		
	}
}
