package fr.an.tests.testjson.rest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.status.Status;

@Service
@Path("/file") // => "/cxf/file"
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class LocalFileRestController {

    @Value("${baseDir:src/test/baseDir}")
    private File baseDir;

    @GET
    @Path("/{path}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("path") String path) {
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

    @POST
    @Path("/{path}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response postFile(@PathParam("path") String path, @QueryParam("content") InputStream content) {
        // Files.write(content, path);
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
            IOUtils.copy(content, fileOut);
        } catch(IOException ex) {
            return Response.status(Status.ERROR).entity("Failed to write file, ex:" + ex.getMessage()).build();
        }
        return Response.ok().build();
    }

}
