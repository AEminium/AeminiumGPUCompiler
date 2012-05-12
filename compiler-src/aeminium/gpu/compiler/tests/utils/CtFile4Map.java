package aeminium.gpu.compiler.tests.utils;

import spoon.contrib.tester.CtFile4BodySnippet;

public class CtFile4Map extends CtFile4BodySnippet {

	public CtFile4Map(String snippet) {
		super(snippet);
	}

	@Override
	protected String makeContent(String snippet) {
		StringBuilder mainb = new StringBuilder();
		mainb.append("import aeminium.gpu.operations.Lambda;");
		mainb.append("import aeminium.gpu.lists.IntList;");

		StringBuilder b = new StringBuilder();

		b.append("IntList l = new IntList();");
		b.append("l.map(new Lambda<Integer, Integer>() {\n");
		b.append("  @Override\n");
		b.append("  public Integer call(Integer input) {\n");
		b.append("    " + snippet.replaceAll("\n", "\n      ") + "\n");
		b.append("  }\n");
		b.append("});\n");

		String fullClass = super.makeContent(b.toString());
		mainb.append(fullClass);
		return mainb.toString();
	}

}
