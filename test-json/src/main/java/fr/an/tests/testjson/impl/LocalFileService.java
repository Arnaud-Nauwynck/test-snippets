package fr.an.tests.testjson.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.qos.logback.core.status.Status;

@Component
public class LocalFileService {
    
    private static final Logger LOG = LoggerFactory.getLogger(LocalFileService.class);
    
    public Response doGetFile(File baseDir, String path) {
        if (path.contains("/../"))
            throw new IllegalArgumentException();
        File file = new File(baseDir, path);
        if (file.exists()) {
            if (file.isDirectory()) {
                return Response.status(Status.ERROR).entity("path already exist as dir").build();
            } // else overwrite file
        } else {
            return Response.status(Status.ERROR).entity("file not found").build();
        }

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                try (InputStream fileIn = new BufferedInputStream(new FileInputStream(file))) {
                    IOUtils.copy(fileIn, os);
                }
            }
        };
        return Response.ok(stream).build();
    }

    public Response doPostFile(File baseDir, String path, InputStream content) {
        if (path.contains("/../"))
            throw new IllegalArgumentException();
        File file = new File(baseDir, path);
        File parentDir = file.getParentFile();
        if (parentDir.exists()) {
            if (!parentDir.isDirectory()) {
                return Response.status(Status.ERROR).entity("parent path already exist as file").build();
            }
        } else {
            parentDir.mkdirs();
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                return Response.status(Status.ERROR).entity("path already exist as file").build();
            } // else overwrite file
        }
        try (OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file))) {
            int resCount = IOUtils.copy(content, fileOut);
            LOG.info("copyed " + resCount + " bytes");
        } catch(IOException ex) {
            return Response.status(Status.ERROR).entity("Failed to write file, ex:" + ex.getMessage()).build();
        }
        return Response.ok().build();
    }
    
}
