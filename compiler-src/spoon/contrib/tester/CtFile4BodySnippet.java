package spoon.contrib.tester;

/**
 * 
 * @author David Bernard <dwayneb@free.fr>
 */
public class CtFile4BodySnippet extends CtFile4SnippetSupport {

    public CtFile4BodySnippet(String snippet) {
        super(snippet);
    }

    @Override
    protected String makeContent(String snippet) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("class " + getClassName() + " {\n");
        buffer.append("\tpublic static void snippet() throws Exception {\n\t");
        buffer.append(snippet + "\n");
        buffer.append("\t}\n");
        buffer.append("}");

        return buffer.toString();
    }

}