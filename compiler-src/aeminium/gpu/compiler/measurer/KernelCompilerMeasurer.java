package aeminium.gpu.compiler.measurer;

import aeminium.gpu.devices.DefaultDeviceFactory;
import aeminium.gpu.devices.GPUDevice;
import aeminium.gpu.executables.Program;

import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;

public class KernelCompilerMeasurer {
	
	private static int TIMES = 3; 
	
	public class KernelCopyProgram implements Program {
		
		private long startTime;	
		private long copyToTime;
		
		protected String getSource() {
			return "__kernel void test(__global int* input, __global int* output) {\n input[get_global_id(0)] = output[get_global_id(0)];\n }\n";
		}
		
		protected CLKernel createKernel(CLProgram program) {
			try {
				return program.createKernel("test");
			} catch (CLBuildException e) {
				e.printStackTrace();
				System.exit(1);
				return null;
			}
		}
		
		protected CLProgram getProgram(CLContext ctx) {
			try {
				if (System.getenv("DEBUG") != null) {
					System.out.println("Compiling Source");
					System.out.println(getSource());
				}
				return ctx.createProgram(getSource()).build();
			} catch (CLBuildException e) {
				e.printStackTrace();
				System.exit(1);
				return null;
			}
		}
		
		@Override
		public void execute(CLContext arg0, CLQueue arg1) {}

		@Override
		public void prepareBuffers(CLContext ctx) {}

		@Override
		public void prepareSource(CLContext ctx) {
			startTime = System.nanoTime();
			CLProgram program = getProgram(ctx);
			createKernel(program);
			copyToTime = System.nanoTime() - startTime;
		}

		@Override
		public void release() {}

		@Override
		public void retrieveResults(CLContext ctx, CLQueue q) {}

		public long getCopyToTime() {
			return copyToTime;
		}
		
	}
	
	public void logMeasures() {
		GPUDevice dev = new DefaultDeviceFactory().getDevice();
		long sumTo = 0;
		
		/* Precompile */
		KernelCopyProgram p = new KernelCopyProgram();
		dev.execute(p);
		
		for(int i=0; i<TIMES; i++) {
			p = new KernelCopyProgram();
			dev.execute(p);
			sumTo += p.getCopyToTime();
		}
		Configuration.set("time.kernel", sumTo/TIMES);
	}
	
	public static void main(String[] args) {
		KernelCompilerMeasurer m = new KernelCompilerMeasurer();
		m.logMeasures();
	}
	
}
