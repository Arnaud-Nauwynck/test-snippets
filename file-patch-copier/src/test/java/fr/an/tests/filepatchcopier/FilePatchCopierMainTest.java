package fr.an.tests.filepatchcopier;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class FilePatchCopierMainTest {

    private FilePatchCopierMain sut = new FilePatchCopierMain();
    private File baseSrcTestDir = new File("src/test");
    private File baseTargetTestDir = new File("target/test");
    
    @Test
    public void testApply() throws IOException {
        // Prepare
        File originSrcDir = new File(baseSrcTestDir,  "dir1");
        File tempDir1 = createCleanTestDir("dir1-tmp", originSrcDir);
        File tempBackupDir1 = createCleanTestDir("dir1-backup", null);
        File patchDir = new File(baseSrcTestDir,  "patch-dir1");
        sut.setTargetDir(tempDir1);
        sut.setBackupDir(tempBackupDir1);
        sut.setPatchDir(patchDir);
        sut.setVerbose(true);
        // Perform
        sut.run();
        // Post-check
        // patched file
        Assert.assertEquals(FileUtils.readFileToString(new File(patchDir, "file1.txt")), FileUtils.readFileToString(new File(tempDir1, "file1.txt")));
        Assert.assertEquals(FileUtils.readFileToString(new File(patchDir, "subdir11/file11.txt")), FileUtils.readFileToString(new File(tempDir1, "subdir11/file11.txt")));
        // file added in patch-only 
        Assert.assertEquals(FileUtils.readFileToString(new File(patchDir, "file3.txt")), FileUtils.readFileToString(new File(tempDir1, "file3.txt")));
        // unmodified file
        Assert.assertEquals(FileUtils.readFileToString(new File(originSrcDir, "file2.txt")), FileUtils.readFileToString(new File(tempDir1, "file2.txt")));
    }

    @Test
    public void testRestore() throws IOException {
        // Prepare
        testApply();
        File originSrcDir = new File(baseSrcTestDir,  "dir1");
        File tempDir1 = new File(baseTargetTestDir, "dir1-tmp");
        File tempBackupDir1 = new File(baseTargetTestDir, "dir1-backup");
        File patchDir = new File(baseSrcTestDir,  "patch-dir1");

        sut.setModeRestoreOrApply(true);
        sut.setTargetDir(tempDir1);
        sut.setBackupDir(tempBackupDir1);
        sut.setPatchDir(patchDir);
        sut.setVerbose(true);
        // Perform
        sut.run();
        // Post-check
        // restored (was patched) file
        Assert.assertEquals(FileUtils.readFileToString(new File(originSrcDir, "file1.txt")), FileUtils.readFileToString(new File(tempDir1, "file1.txt")));
        Assert.assertEquals(FileUtils.readFileToString(new File(originSrcDir, "subdir11/file11.txt")), FileUtils.readFileToString(new File(tempDir1, "subdir11/file11.txt")));
        // .. file added in patch-only => should be removed!
        // TODO Assert.assertFalse(new File(tempDir1, "file3.txt").exists());
        // unmodified file
        Assert.assertEquals(FileUtils.readFileToString(new File(originSrcDir, "file2.txt")), FileUtils.readFileToString(new File(tempDir1, "file2.txt")));
    }
    
    protected File createCleanTestDir(String dir, File src) throws IOException {
        File res = new File(baseTargetTestDir, dir);
        if (res.exists()) {
            FileUtils.deleteDirectory(res);
        }
        res.mkdirs();
        if (src != null) {
            FileUtils.copyDirectory(src, res);
        }
        return res;
    }
    
}
