package fr.an.test.eclipsemirror;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class IndexHtmlGenMain {

    private File inputDir;
    private Pattern inputFilePattern;
    private String outputFilename;
    private File outputDir;
    
    // ------------------------------------------------------------------------

    
    public static void main(String[] args) {
        IndexHtmlGenMain app = new IndexHtmlGenMain();
        app.parseArgs(args);
        app.run();
    }

    // ------------------------------------------------------------------------
    
    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("-i")) {
                inputFilePattern = Pattern.compile(args[++i]);
            } else if (a.equals("-o")) {
                outputFilename = args[++i];
            } else if (a.equals("-d")) {
                inputDir = new File(args[++i]);
            } else if (a.equals("--outDir")) {
                outputDir = new File(args[++i]);
            }
        }
        if (inputDir == null) {
            inputDir = new File(".");
        }
        if (outputDir == null) {
            outputDir = inputDir;
        }
        if (inputFilePattern == null) {
            inputFilePattern = Pattern.compile(".*-split-.*\\.zip");
        }
        if (outputFilename == null) {
            outputFilename = "index.html";
        }
    }
    
    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n<body>\n");
        sb.append("<ul>\n");
        
        File[] files = inputDir.listFiles();
        for(File f : files) {
            String fileName = f.getName();
            if (inputFilePattern.matcher(fileName).matches()) {
                sb.append("<li><img src='" + fileName + "' width='1' height='1'/></li>\n");
            }
        }
        
        sb.append("</ul>\n");
        sb.append("</body>\n</html>\n");
        
        try {
            FileUtils.write(new File(outputDir, outputFilename), sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed", e);
        }
    }
    
}
