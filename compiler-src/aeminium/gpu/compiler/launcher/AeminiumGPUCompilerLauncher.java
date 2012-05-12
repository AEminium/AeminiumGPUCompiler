package aeminium.gpu.compiler.launcher;

import java.io.File;
import java.io.IOException;
import java.util.List;

import spoon.Launcher;
import spoon.support.builder.CtResource;
import spoon.support.builder.support.CtFolderFile;
import aeminium.gpu.compiler.processing.MapLambdaProcessor;
import aeminium.gpu.compiler.processing.ReduceLambdaProcessor;

import com.martiansoftware.jsap.JSAPException;

public class AeminiumGPUCompilerLauncher extends Launcher {

	public AeminiumGPUCompilerLauncher(String[] args) throws JSAPException {
		super(args);
	}

	@Override
	protected List<String> getProcessorTypes() {
		List<String> l = super.getProcessorTypes();
		l.add(MapLambdaProcessor.class.getName());
		l.add(ReduceLambdaProcessor.class.getName());
		return l;
	}

	@Override
	protected List<CtResource> getTemplateSources() {
		List<CtResource> l = super.getTemplateSources();
		try {
			l.add(new CtFolderFile(new File(
					"compiler-src/aeminium/gpu/compiler/template")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l;
	}

	public static void main(String[] args) {
		try {
			AeminiumGPUCompilerLauncher launcher = new AeminiumGPUCompilerLauncher(
					args);
			launcher.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
