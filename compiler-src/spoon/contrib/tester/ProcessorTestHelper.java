package spoon.contrib.tester;

import java.io.File;
import java.io.IOException;

import spoon.processing.Builder;
import spoon.processing.Environment;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.builder.CtFile;
import spoon.support.builder.SpoonBuildingManager;
import spoon.support.builder.support.CtFolderFile;

/**
 * 
 * @author David Bernard <dwayneb@free.fr>
 * @author Lionel Seinturier <Lionel.Seinturier@lifl.fr>
 */
public class ProcessorTestHelper {

    public static void assertTransform(Processor<?> p, CtFile src, CtFile expected) throws Exception {
        Environment env = new StandardEnvironment();
        Factory factory = new Factory(new DefaultCoreFactory(), env);
        DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(env);

        String result = transform(p, factory, src, printer);
        String expectedFormatted = transform(null, factory, expected, printer);

        if (!expectedFormatted.equals(result)) {
            throw new AssertionError("expected:<" + expectedFormatted + "> but was:<" + result + ">");
        }
    }

    public static CtSimpleType<?> transform(Processor<?> p, CtFile src) throws Exception {
    	Environment env = new StandardEnvironment();
    	env.setVerbose(true);
        Factory factory = new Factory(new DefaultCoreFactory(), env);
        CtSimpleType<?> result = transform(p, factory, src);
        return result;
    }

    private static CtSimpleType<?> transform(Processor<?> p, Factory  factory, CtFile src) throws Exception {
//        List<CtFile> l = new ArrayList<CtFile>();
//        l.add(src);
//        JDTCompiler jdtc = new JDTCompiler();
//        jdtc.compileSrc(factory, l);
//        List s = factory.Type().getAll();
//        String targetClassname = src.getName();
//        CtClass<?> ctclass = (CtClass<?>) factory.Type().get(targetClassname);
//        return ctclass;

        Builder pbuilder = new SpoonBuildingManager(factory);
        pbuilder.addInputSource(src);
        prepareBuilder(pbuilder);
        pbuilder.build();
        CtSimpleType<?> back = factory.Type().getAll().get(0);
        if (back == null) {
            throw new IllegalArgumentException("failed to read 'src'");
        }
        if (p != null) {
            ProcessingManager pm = new QueueProcessingManager(factory);
            pm.addProcessor(p);
            pm.process(back);
        }
        return back;
    }

    public static void prepareBuilder(Builder pbuilder) throws IOException {
    	pbuilder.addTemplateSource(new CtFolderFile(new File("compiler-src/aeminium/gpu/compiler/template")));
	}

	private static String transform(Processor<?> p, Factory  factory, CtFile src, DefaultJavaPrettyPrinter printer) throws Exception {
        CtSimpleType<?> back = transform(p, factory, src);
        printer.getResult().setLength(0);
        printer.scan(back);
        return printer.getResult().toString();
    }
}