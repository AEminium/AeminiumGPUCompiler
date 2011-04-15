package aeminium.gpu.compiler.measurer;

import aeminium.gpu.buffers.BufferHelper;
import aeminium.gpu.devices.DefaultDeviceFactory;
import aeminium.gpu.devices.GPUDevice;
import aeminium.gpu.executables.Program;
import aeminium.gpu.lists.CharList;
import aeminium.gpu.lists.DoubleList;
import aeminium.gpu.lists.FloatList;
import aeminium.gpu.lists.IntList;
import aeminium.gpu.lists.LongList;
import aeminium.gpu.lists.PList;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLQueue;

public class BufferTransferMeasurer {
	
	int[] sizes = new int[] {10, 100, 1000, 10000, 100000, 1000000, 10000000};
	
	private static int TIMES = 3; 
	
	public class CopyProgram implements Program {
		
		private long startTime;
		private long copyToTime;
		private long copyFromTime;
		private CLBuffer<?> inbuffer;
		PList<?> input;
		
		public CopyProgram(PList<?> input) {
			this.input = input;
		}

		@Override
		public void execute(CLContext arg0, CLQueue arg1) {}

		@Override
		public void prepareBuffers(CLContext ctx) {
			startTime = System.nanoTime();
			inbuffer = BufferHelper.createInputOutputBufferFor(ctx, input);
			copyToTime = System.nanoTime() - startTime;
			input.clear();
		}

		@Override
		public void prepareSource(CLContext arg0) {}

		@Override
		public void release() {}

		@SuppressWarnings("unused")
		@Override
		public void retrieveResults(CLContext ctx, CLQueue q) {
			startTime = System.nanoTime();
			PList<?> out = BufferHelper.extractFromBuffer(inbuffer, q, null, input.size(), input);
			copyFromTime = System.nanoTime() - startTime;
		}

		public long getCopyToTime() {
			return copyToTime;
		}

		public long getCopyFromTime() {
			return copyFromTime;
		}
		
	}
	
	public void logMeasures() {
		GPUDevice dev = new DefaultDeviceFactory().getDevice();
		PList<?> in = new IntList();
		for (int size: sizes) {
			for (int t=0;t<5;t++) {
				switch (t) {
				case 0:
					in = new IntList(new int[size], size);
					break;
				case 1:
					in = new FloatList(new float[size], size);
					break;
				case 2:
					in = new DoubleList(new double[size], size);
					break;
				case 3:
					in = new LongList(new long[size], size);
					break;
				case 4:
					in = new CharList(new char[size], size);
					break;
				}
				long sumTo = 0;
				long sumFrom = 0;
				for(int i=0; i<TIMES; i++) {
					CopyProgram p = new CopyProgram(in);
					dev.execute(p);
					sumTo += p.getCopyToTime();
					sumFrom += p.getCopyFromTime();
				}
				
				Configuration.set("time.buffer." + in.getType().getSimpleName() + ".to." + size, sumTo/TIMES);
				Configuration.set("time.buffer." + in.getType().getSimpleName() + ".from." + size, sumFrom/TIMES);
			}
		}
	}
	
	public static void main(String[] args) {
		BufferTransferMeasurer m = new BufferTransferMeasurer();
		m.logMeasures();
	}
	
}
