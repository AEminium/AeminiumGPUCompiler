package spoon.contrib.tester;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;

/**
 * 
 * @author David Bernard <dwayneb@free.fr>
 * @author Lionel Seinturier <Lionel.Seinturier@lifl.fr>
 */
public abstract class CtFile4SnippetSupport implements CtFile {
    
    private String content;
    private String className;
    private File file;

    public CtFile4SnippetSupport(String snippet) {
        this(null, snippet);
    }

    public CtFile4SnippetSupport(String className, String snippet) {
        super();

        if (snippet == null) {
            throw new IllegalArgumentException("snipplet is null");
        }

        /*
         * Set className.
         */
        if (className == null) {
            try {
                file = File.createTempFile("Spoon", ".java");
                file.deleteOnExit();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.className = file.getName();
            this.className =    // remove .java
                this.className.substring(0,this.className.length()-5);
        }
        else {
            String s = className.replace('.','/')+".java";
            file = new File(s);
            this.className = className;
        }
        content = makeContent(snippet);

        /*
         * Dump the content to the temporary file when needed.
         */
        if (className == null) {
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(content);
                fw.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(content.getBytes());
    }

    public boolean isJava() {
        return true;
    }

    /*
     */
    public String getPath() {
        return file.getPath();
    }
    
    public String getName() {
        String fname = className.replace('.', '/');
        fname = fname + ".java";
        return fname;
    }

    public String getClassName() {
        return className.substring(className.lastIndexOf('.')+1);
    }

    public String getFullClassName() {
        return className;
    }

    public CtFolder getParent() {
        return null;
    }

    public boolean isFile() {
        return true;
    }

    protected abstract String makeContent(String snippet);

    @Override
    public String toString() {
        return content;
    }
}