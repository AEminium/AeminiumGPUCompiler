package spoon.contrib.tester;

/**
 * 
 * @author David Bernard <dwayneb@free.fr>
 */
public class CtFile4ClassSnippet extends CtFile4SnippetSupport {

    public CtFile4ClassSnippet(String fullClassName, String snippet) throws Exception {
        super(fullClassName, snippet);
    }

    @Override
    protected String makeContent(String snippet) throws IllegalArgumentException {
        if (snippet.trim().length() == 0) {
            throw new IllegalArgumentException("snippet is empty");
        }
        return snippet;
    }

}