package fr.an.test.eclipsemirror;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipSplitterMain {

    private String inputFilename;
    private String outputFilename;
    private int maxPartLen = 1*1024*1024; // 1 Mo
    private String extName = ".png"; //".zip";
    
    public static void main(String[] args) {
        try {
            ZipSplitterMain app = new ZipSplitterMain();
            app.parseArgs(args);
            app.run();
            
            System.out.println("Finished");
        } catch(Exception ex) {
            System.err.println("Failed ..exiting");
            ex.printStackTrace(System.err);
        }
    }


    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("-i")) {
                inputFilename = args[++i];
            } else if (a.equals("-o")) {
                outputFilename = args[++i];
            } else if (a.equals("-max")) {
                maxPartLen = Integer.parseInt(args[++i]);
            }
        }
        if (outputFilename == null) {
            outputFilename = inputFilename;
            if (outputFilename.endsWith(".zip")) {
                outputFilename = outputFilename.substring(0, outputFilename.length()-4);
            }
            outputFilename += "-split";
        }
    }
    
    public void run() {
        File zipFile = new File(inputFilename);
        int currentZipOutputIndex = 0;
        File currentZipOutputFile = new File(outputFilename + "-" + currentZipOutputIndex + extName);
        ZipOutputStream currentZos = null;
        
        byte[] buffer = new byte[16*4096];
        long currentLen = 0;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
    
            currentZos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(currentZipOutputFile)));
                    
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){
               String zeFileName = ze.getName();
               
               ZipEntry outZe = new ZipEntry(zeFileName);
               currentZos.putNextEntry(outZe);
               int len;
               while ((len = zis.read(buffer)) > 0) {
                   currentZos.write(buffer, 0, len);
               }
               currentZos.closeEntry();

               if (! zeFileName.endsWith("/")) {
                   long entryLen = ze.getCompressedSize();
                   currentLen += entryLen;
                        
                   System.out.println(zeFileName + " +" + (entryLen/1024) + " => " + (currentLen/1024));
               }

               if (currentLen > maxPartLen) {
                   // flush part
                   currentLen = 0;
                   currentZos.close();
                   
                   ++currentZipOutputIndex;
                   currentZipOutputFile = new File(outputFilename + "-" + currentZipOutputIndex + extName);
                   currentZos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(currentZipOutputFile)));
               }
               
               ze = zis.getNextEntry();
            }
            
            currentZos.close();
            
            zis.closeEntry();
            
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
    }
}
