package fr.an.test.eclipsemirror;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SteganoDecodeMainTest {

    @Test
    public void testRun_test1_decodeZipOnly() throws Exception {
        // Prepare
        File inputDir = new File("src/test/test1");
        File outputDir = new File("target/tests/test1-decodeZipOnly");
        if (outputDir.exists()) {
            FileUtils.deleteDirectory(outputDir);
        }
        outputDir.mkdirs();
        
        SteganoDecodeMain sut = new SteganoDecodeMain();
        sut.setInputDir(inputDir);
        sut.setInputFileBaseName("test1");
        sut.setInputFileExt(".png");
        sut.setOutputDir(outputDir);
        sut.setDecodeZipOnly(true);
        sut.setOutputFilename("test1");
        // Perform
        sut.run();
        // Post-check
        Assert.assertEquals(3, outputDir.listFiles().length); 
        Assert.assertTrue(new File(outputDir, "test1-0.zip").exists());
        Assert.assertTrue(new File(outputDir, "test1-1.zip").exists());
        Assert.assertTrue(new File(outputDir, "test1-2.zip").exists());
    }

    
    @Test
    public void testRun_test1_decoded() throws Exception {
        // Prepare
        File inputDir = new File("src/test/test1");
        File outputDir = new File("target/tests/test1-decoded");
        if (outputDir.exists()) {
            FileUtils.deleteDirectory(outputDir);
        }
        outputDir.mkdirs();
        
        SteganoDecodeMain sut = new SteganoDecodeMain();
        sut.setInputDir(inputDir);
        sut.setInputFileBaseName("test1");
        sut.setInputFileExt(".png");
        sut.setOutputDir(outputDir);
        // Perform
        sut.run();
        // Post-check
        File[] listFiles = outputDir.listFiles();
        Assert.assertEquals(2, listFiles.length);
        Assert.assertTrue(new File(outputDir, "main").exists()); 
        Assert.assertTrue(new File(outputDir, "test").exists()); 
    }

    
    @Test @Ignore
    public void testRun_test1_huge() throws Exception {
        // Prepare
        File inputDir = new File("target/tests/test_huge");
        File outputDir = new File("target/tests/test_huge-decoded");
        if (outputDir.exists()) {
            FileUtils.deleteDirectory(outputDir);
        }
        outputDir.mkdirs();
        
        SteganoDecodeMain sut = new SteganoDecodeMain();
        sut.setInputDir(inputDir);
        sut.setInputFileBaseName("test1");
        sut.setInputFileExt(".png");
        sut.setOutputDir(outputDir);
        // Perform
        sut.run();
        // Post-check
        File[] listFiles = outputDir.listFiles();
        Assert.assertEquals(1, listFiles.length);
        File pluginsDir = new File(outputDir, "plugins");
        Assert.assertTrue(pluginsDir.exists()); 
        Assert.assertEquals(812, pluginsDir.list().length);
    }
    
    
}
