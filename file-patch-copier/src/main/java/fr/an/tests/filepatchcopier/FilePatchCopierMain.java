package fr.an.tests.filepatchcopier;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import lombok.Getter;
import lombok.Setter;

/**
 * minimalistic file-patch-copier (no jar dependency, single class)
 *
 */
public class FilePatchCopierMain {

    @Getter @Setter
    private File targetDir;
    @Getter @Setter
    private File patchDir;
    @Getter @Setter
    private File backupDir;
    @Getter @Setter
    private boolean modeRestoreOrApply;
    @Getter @Setter
    private boolean verbose;
    
    // ------------------------------------------------------------------------

    public FilePatchCopierMain() {
    }

    // ------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            FilePatchCopierMain app = new FilePatchCopierMain();
            app.parseArgs(args);
            app.run();
            System.out.println("Finished");
        } catch(Exception ex) {
            System.err.println("Failed exiting (-1)");
            System.exit(-1);
        }
    }

    private void parseArgs(String[] args) {
        for(int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("--restore")) {
                modeRestoreOrApply = true;
            } else if (a.equals("-d") || a.equals("--targetDir")) {
                targetDir = new File(args[++i]);
            } else if (a.equals("-p") || a.equals("--patchDir")) {
                patchDir = new File(args[++i]);
            } else if (a.equals("-b") || a.equals("--backupDir")) {
                backupDir = new File(args[++i]);
            } else {
                System.out.println("usage: FilePatchCopierMain [--restore]" 
                        + " (-d | --targetDir) dir"
                        + " (-p || --patchDir) dir"
                        + " [(-b || --backupDir) dir]"
                        );
                throw new RuntimeException("Unrecognised argument '" + a + "'");
            }
        }
    }
    
    public void run() {
        checkFileExistAndIsDir(patchDir);
        checkFileExistAndIsDir(targetDir);
        try {
            if (modeRestoreOrApply) {
                if (backupDir.exists()) {
                    File backupRestore = null; // should be useless?
                    recurseCopyWithBackup("", backupDir, targetDir, backupRestore);
                }
            } else {
                recurseCopyWithBackup("", patchDir, targetDir, backupDir);
            }
        } catch(IOException ex) {
            throw new RuntimeException("Failed", ex);
        }
    }

    private void recurseCopyWithBackup(String relativePath, File src, File dest, File backup) throws IOException {
        if (verbose) {
            System.out.println("processing " + relativePath);
        }
        if (src.isDirectory()) {
            if (! dest.exists()) {
                dest.mkdirs();
            }
            // recurse
            for (File childPatchFile : src.listFiles()) {
                String childName = childPatchFile.getName();
                String childRelativePath = ((relativePath.length()!=0)? "/" : "") + childName;
                File childTargetFile = new File(dest, childName);
                File childBackupFile = (backup != null)? new File(backup, childName) : null;
                
                recurseCopyWithBackup(childRelativePath, childPatchFile, childTargetFile, childBackupFile);
            }
        } else {
            if (dest.exists()) {
                // backup file
                if (backup != null) {
                    File backupDestDir = backup.getParentFile();
                    backupDestDir.mkdirs();
                    // targetFile.renameTo(backupFile); // may not copy from different filesystems..
                    copyFile(dest, backup);
                }
            }
            // copy patch file to target
            copyFile(src, dest);
        }
    }



    private static void checkFileExistAndIsDir(File dir) {
        if (! dir.exists()) {
            throw new RuntimeException("dir not found : '" + dir + "'");
        }
        if (! dir.isDirectory()) {
            throw new RuntimeException("not a directory file: '" + dir + "'");
        }
    }
    
    private void copyFile(File srcFile, File destFile) {
        String copyMsg = "copy '" + srcFile + "'  '" + destFile + "'";
        if (verbose) {
            System.out.println(copyMsg);
        }
        try {
            doCopyFile(srcFile, destFile, true);
        } catch(Exception ex) {
            throw new RuntimeException("Failed " + copyMsg, ex);
        }
    }
    
    // cf apache common-io FileUtils !
    private static final int FILE_COPY_BUFFER_SIZE = 1024*1024*30;
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input  = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            closeQuietly(output);
            closeQuietly(fos);
            closeQuietly(input);
            closeQuietly(fis);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch(Exception ex) {
                System.out.println("Failed to close file resource... ignore, no rethrow");
            }
        }
    }
    
}
