package fr.an.test.eclipsemirror;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import fr.an.bitwise4j.util.ByteBufferOutputStream;

public class IndexedFilesZipper {

    private int maxPartLen;
    
    private int currentZipOutputIndex = 0;
    
    private int allocSize;
    private ByteBuffer buffer;
    private ByteBufferOutputStream bufferOut;
    private ZipOutputStream currentZipOut;
    private long currentZipLen = 0;
    
    public IndexedFilesZipper(int maxPartLen) {
        this.maxPartLen = maxPartLen;
        allocSize = 5*maxPartLen + 10*1024*1024; //TODO should check realloc 
        buffer = ByteBuffer.allocate(allocSize);
        bufferOut = new ByteBufferOutputStream(buffer);
        currentZipOut = new ZipOutputStream (bufferOut);
    }

    public void putNextEntry(String relativePathName, File file, BiConsumer<ByteBuffer,Integer> flushZipPartFunc) {
        try (FileInputStream fIn = new FileInputStream(file)) {
            long fileLen = file.length();
            
            ZipEntry outZe = new ZipEntry(relativePathName);
            currentZipOut.putNextEntry(outZe);
            IOUtils.copy(fIn, currentZipOut);
            currentZipOut.closeEntry();

            currentZipLen += fileLen;
            //     System.out.println(zeFileName + " +" + (fileLen/1024) + " => " + (currentLen/1024));
            if (currentZipLen > maxPartLen) {
                // flush part
                currentZipOut.close();
                
                buffer.flip();
                
                flushZipPartFunc.accept(buffer, currentZipOutputIndex);
                              
                buffer.clear();
                
                ++currentZipOutputIndex;
                currentZipOut = // new ZipOutputStream(bufferOut);
                        new ZipOutputStream(new ByteBufferOutputStream(buffer));
                currentZipLen = 0;
            }
            
        } catch(IOException ex) {
            throw new RuntimeException("Failed", ex);
        }
    }
    
}
