package aeminium.gpu.compiler.processing.estimation;

import java.util.HashMap;
import java.util.Iterator;

import aeminium.gpu.utils.Pair;

public class EstimationStore implements Estimation,
		Iterable<Pair<Integer, String>> {

	HashMap<String, Integer> store = new HashMap<String, Integer>();

	@Override
	public void addEstimation(String expr) {
		addEstimation(expr, 1);
	}

	@Override
	public void addEstimation(String expr, int times) {
		if (expr.equals("le") || expr.equals("postinc")) {
			expr = "plus";
		}
		if (store.containsKey(expr)) {
			times += store.get(expr);
		}
		store.put(expr, times);
	}

	@Override
	public Iterator<Pair<Integer, String>> getEstimations() {
		return iterator();
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		int i = 0;
		for (String k : store.keySet()) {
			b.append(store.get(k));
			b.append("*");
			b.append(k);
			i++;
			if (i < store.size()) {
				b.append("+");
			}
		}
		return b.toString();
	}

	@Override
	public Iterator<Pair<Integer, String>> iterator() {
		final Iterator<String> storeIter = store.keySet().iterator();
		return new Iterator<Pair<Integer, String>>() {

			@Override
			public boolean hasNext() {
				return storeIter.hasNext();
			}

			@Override
			public Pair<Integer, String> next() {
				String expr = storeIter.next();
				return new Pair<Integer, String>(store.get(expr), expr);
			}

			@Override
			public void remove() {
				storeIter.remove();
			}

		};
	}

}
