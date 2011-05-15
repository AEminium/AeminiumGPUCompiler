package aeminium.gpu.compiler.processing.estimation;

import java.util.Iterator;

import aeminium.gpu.utils.Pair;

public interface Estimation {
	public void addEstimation(String expr);
	public void addEstimation(String expr, int times);
	public Iterator<Pair<Integer, String>> getEstimations();
}
