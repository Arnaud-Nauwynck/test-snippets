package fr.an.test.eclipsemirror;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class PngWrapperMain {

    private File inputDir;
    private Pattern inputFilePattern;
    private String outputFilename;
    private File outputDir;
    
    // ------------------------------------------------------------------------

    
    public static void main(String[] args) {
        PngWrapperMain app = new PngWrapperMain();
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
        int allocSize = 20*1024*1024;
        ByteBuffer buffer = ByteBuffer.allocate(allocSize);
        
        File[] files = inputDir.listFiles();
        for(File f : files) {
            String fileName = f.getName();
            if (inputFilePattern.matcher(fileName).matches()) {
                buffer.reset();
                
                readFileFully(f, buffer);
                
                buffer.flip();
                int fileLen = buffer.remaining();
                
                // copy buffer to img
                int imgW = (int) Math.min(Math.sqrt(fileLen), 1024);
                int imgH = (int) (fileLen + imgW-1) / imgW;
                BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_4BYTE_ABGR);
                DataBufferByte imgDataBuffer = (DataBufferByte) img.getRaster().getDataBuffer();
                byte[] imgData = imgDataBuffer.getData();
                buffer.get(imgData, 0, fileLen);
                
                // long fileLenWithPad = imgW*imgH; 
                // for (int i = fileLen; i < fileLenWithPad; i++) {
                // }
                
                // write(encode) img file
                File fileOutput = new File(outputDir, fileName + ".PNG");
                try {
                    ImageIO.write(img, "PNG", fileOutput);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to write file " + fileOutput, ex);
                }
                
                buffer.reset();
            }
        }
    }

    public static void readFileFully(File f, ByteBuffer buffer) {
        try {
            FileInputStream in = new FileInputStream(f);
            readFully(in.getChannel(), buffer);
            in.close();
        } catch(IOException ex) {
            throw new RuntimeException("Failed to read file " + f + " " + ex.getMessage());
        }
    }
    
    public static void readFully(FileChannel file, ByteBuffer buf) throws IOException {
        long toRead = file.size();
        while (toRead > 0) {
            long count = file.read(buf);
            if (count == -1) {
                throw new RuntimeException("EOF .. expecting " + toRead + " bytes");
            } else {
                toRead -= count;
            }
        }
    }
    
}
