package benchmark;
import aeminium.gpu.collections.lazyness.Range;
import aeminium.gpu.collections.lists.FloatList;
import aeminium.gpu.collections.lists.PList;
import aeminium.gpu.operations.functions.LambdaMapper;
import aeminium.gpu.operations.functions.LambdaReducer;

public class BenchmarkDecisions {
	
	public static int MAX_LEVEL=10000000;
	public static int MAX_TIMES=1;
	
	public static void main(String[] args) {
	    int size = 10;
	    int inc = 10;
	    while (size < MAX_LEVEL) {
	        runForNTimes(size);
	        size += inc;
	        if (size >= 10*inc) {
	            inc *= 10;
	        }
	    }
	}

	private static void runForNTimes(int N) {
		System.out.println("Testing GPU vs GPU for N="+N);
		for (int i = 0;i<MAX_TIMES; i++) {
			runForN(N);
		}
	}
	
	private static void runForN(int N) {
		PList<Float> output;
		PList<Float> input = new FloatList();
		for (int i = 0; i < N; i++) {
			input.add((float) i);
		}
		
		/* UNIT */
		System.out.println("> GPU op: unit");
		output = input.map(new LambdaMapper<Float, Float>() {

			@Override
			public Float map(Float input) {
				return input + 1;
			}
			
		});
		output.get(0);
		
		sleep();
		
		/* SIN */
		System.out.println("> GPU op: sin " + input.size());
		output = input.map(new LambdaMapper<Float, Float>() {

			@Override
			public Float map(Float input) {
				return (float) Math.sin(input);
			}
			
		});
		output.get(0);
		
		sleep();
		
		/* SIN and COS */
		System.out.println("> GPU op: sin+cos " + input.size());
		output = input.map(new LambdaMapper<Float, Float>() {

			@Override
			public Float map(Float input) {
				return (float) (Math.sin(input) + Math.cos(input));
			}
			
		});
		output.get(0);
		
		sleep();
		
		/* Factorial */
		System.out.println("> GPU op: factorial " + input.size());
		output = input.map(new LambdaMapper<Float, Float>() {

			@Override
			public Float map(Float input) {
				int r = 1;
				for (int i=1;i<=10000; i++) {
					r *= i;
				}
				return (float) r;
			}
			
		});
		output.get(0);
		
		sleep();
		
		/* Integral */
		System.out.println("> GPU op: factorial " + input.size());
		PList<Integer> li = new Range(input.size());
		
		PList<Double> li2 = li.map(new LambdaMapper<Integer, Double>() {

			@Override
			public Double map(Integer input) {
				double n = 10000000.0;
				double b = Math.pow(Math.E, Math.sin(input / n));
				double B = Math.pow(Math.E, Math.sin((input+1) / n));
				return ((b+B) / 2 ) * (1/n);
			}
			
		});
		
		@SuppressWarnings("unused")
		double output2 = li2.reduce(new LambdaReducer<Double>(){

			@Override
			public Double combine(Double input, Double other) {
				return input + other;
			}
			
			@Override
			public Double getSeed() {
				return 0.0;
			}
			
		});
		
		sleep();
		
		/* Integral */
		System.out.println("> GPU op: fminimum " + input.size());
		final double RESOLUTION = (double) input.size();
		output2 = new Range((int)RESOLUTION).map(new LambdaMapper<Integer, Double>() {

			@Override
			public Double map(Integer input) {
				double x = 2*input/(double)(RESOLUTION) - 1;
				return 10 * Math.pow(x, 6) + Math.pow(x, 5)  + 2 * Math.pow(x, 4) + 3 * x * x * x + 2/5*x*x +Math.PI * x;
			}
			
		}).reduce(new LambdaReducer<Double>(){

			@Override
			public Double combine(Double input, Double other) {
				return Math.min(input,other);
			}
			
			@Override
			public Double getSeed() {
				return Double.MAX_VALUE;
			}
			
		});
		
		sleep();
	}

	private static void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
