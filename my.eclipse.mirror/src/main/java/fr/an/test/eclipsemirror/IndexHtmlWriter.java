package fr.an.test.eclipsemirror;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class IndexHtmlWriter {
    File outputDir;
    String outputFilename;
    PrintStream indexHtmlOut;
    int imgPageCount = 0;
    int maxImgPerPage = 10;
    int pageIndex = 0;
    PrintStream pageHtmlOut;
    
    public IndexHtmlWriter(File outputDir, String outputFilename) {
        this.outputDir = outputDir;
        this.outputFilename = outputFilename;
        indexHtmlOut = newFilePrintStream(outputFilename + ".html");
        indexHtmlOut.print("<html>\n<body>\n");
        
        openPageHtml();
    }

    private void openPageHtml() {
        String pageName = outputFilename + "-" + pageIndex + ".html";
        pageHtmlOut = newFilePrintStream(pageName);
        pageHtmlOut.print("<html>\n<body>\n");
        
        indexHtmlOut.println("<A href='" + pageName + "'>page</A>\n");
    }

    protected PrintStream newFilePrintStream(String fileName) {
        try {
            return new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(outputDir, fileName))));
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void addImgFile(String imgFileName) {
        imgPageCount++;
        pageHtmlOut.print("<img src='" + imgFileName + "' width='20' height='20'/>\n");
        if (imgPageCount > maxImgPerPage) {
            closePageHtml();
            imgPageCount = 0;
            pageIndex++;
            openPageHtml();
        }
    }
    
    public void close() {
        indexHtmlOut.print("</body>\n</html>\n");
        indexHtmlOut.close();
        indexHtmlOut = null;
        
        closePageHtml();
    }

    private void closePageHtml() {
        pageHtmlOut.print("</body>\n</html>\n");
        pageHtmlOut.close();
        pageHtmlOut = null;
    }
    
}