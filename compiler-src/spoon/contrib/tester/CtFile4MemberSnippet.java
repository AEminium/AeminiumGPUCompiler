package spoon.contrib.tester;

/**
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@lifl.fr>
 */
public class CtFile4MemberSnippet extends CtFile4SnippetSupport {

    public CtFile4MemberSnippet(String snippet) {
        super(snippet);
    }

    @Override
    protected String makeContent(String snippet) {
        StringBuilder buffer = new StringBuilder();
        
        /*
         * Do not define the class as public.
         * public was working with Spoon 20061202.
         * 
         * Between 1202 and 1230, the way Spoon deals with CompilationUnit has
         * changed. A consequence is that the {@link CtFile4SnippetSupport}
         * class must provide a getPath() method (see {@link
         * CtFile4SnippetSupport#getPath()}).
         * 
         * If the class were public, JDT would check that the corresponding file
         * would exist (which is of course not the case as we deal with
         * snippets).
         */
        
        buffer.append("class " + getClassName() + " {\n");
        buffer.append(snippet + "\n");
        buffer.append("}");

        return buffer.toString();
    }

}