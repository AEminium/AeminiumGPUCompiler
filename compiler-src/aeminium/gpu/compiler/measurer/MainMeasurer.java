package aeminium.gpu.compiler.measurer;

public class MainMeasurer {
	
	public static void recordTimes() {
		BufferTransferMeasurer btm = new BufferTransferMeasurer();
		btm.logMeasures();
		
		KernelCompilerMeasurer ktm = new KernelCompilerMeasurer();
		ktm.logMeasures();
		
		ExpressionCostMeasurer ecm = new ExpressionCostMeasurer();
		ecm.measureExprs();
	}
	
	public static void main(String[] args) {
		recordTimes();
	}
}
