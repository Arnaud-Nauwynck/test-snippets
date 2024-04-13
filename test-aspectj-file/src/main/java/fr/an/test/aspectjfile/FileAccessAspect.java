package fr.an.test.aspectjfile;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.io.FileDescriptor;
import java.nio.file.OpenOption;

@Aspect  // TODO does not work !!!! can not instrument "java.*"
public class FileAccessAspect extends BaseAspect {

    /** see {@link java.io.FileInputStream } */
    @Before("call(java.io.FileInputStream.new(String)) && args(file)")
    public void before_new_FileInputStream_String(String file) {
        logAspect("before", "new FileInputStream( '" + file + "')");
    }

    @Before("call(java.io.FileInputStream.new(java.io.FileDescriptor)) && args(fd)")
    public void before_new_FileInputStream_FileDescriptor(FileDescriptor fd) {
        logAspect("before", "new FileInputStream(FileDescriptor: '" + fd + "')");
    }

    @Before("call(java.nio.channels.FileChannel java.nio.file.Files.newByteChannel(java.nio.file.Path, java.util.Set, java.nio.file.attribute.FileAttribute)) && args(path, options, attributes)")
    public void before_java_nio_Files_newByteChannel(java.nio.file.Path path, java.util.Set<? extends OpenOption> options, java.nio.file.attribute.FileAttribute<?>... attributes) {
        logAspect("before", "java.nio.file.Files.newByteChannel(path: '" + path + "')");
    }


}
