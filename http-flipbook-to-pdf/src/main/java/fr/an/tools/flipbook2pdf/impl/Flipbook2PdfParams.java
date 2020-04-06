package fr.an.tools.flipbook2pdf.impl;

import java.io.File;

import lombok.Data;

@Data
public class Flipbook2PdfParams {

    private File baseOutputDir;
    
    private String orderDetail;
    private String bookTitle;

    
    private boolean trustAllCerts = true;
    private String httpProxyHost = null; // "localhost";
    private int httpProxyPort = 8080;
    
    private boolean debugHttpCalls = false;

    
    public void parseArgs(String[] args) {
        for(int i = 0; i < args.length; i++) {
            switch(args[i]) {
            case "-o": case "--outputDir":
                this.baseOutputDir = new File(args[++i]);
                break;
            case "--orderDetail":
                this.orderDetail = args[++i];
                break;
            default:
                throw new IllegalArgumentException("Unrecognized argument '" + args[i] + "'");
            }
        }
        
        if (baseOutputDir == null) {
            baseOutputDir = new File("out");
        }
        if (! baseOutputDir.exists()) {
            baseOutputDir.mkdirs();
        }
    }

}
