package fr.an.tests.testjson.rest;

import java.io.File;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.an.tests.testjson.impl.LocalFileService;

@Service
@Path("/file") // => "/cxf/file"
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class LocalFileRestController {
    
    private static final Logger LOG = LoggerFactory.getLogger(LocalFileRestController.class);
    
    @Inject
    private LocalFileService localFileService;
    
    @Value("${baseDir:src/test/baseDir}")
    private File baseDir;

    @GET
    @Path("/{path}/content")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("path") String path) {
        LOG.info("getFile '" + path + "'");
        return localFileService.doGetFile(baseDir, path);
    }

    @GET
    @Path("/{path}/contentText")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFileTextPlain(@PathParam("path") String path) {
        LOG.info("getFileTestPlain '" + path + "'");
        return localFileService.doGetFile(baseDir, path);
    }
    
    @POST
    @Path("/{path}/content")
    @Consumes(MediaType.WILDCARD)
    public Response postFile(@PathParam("path") String path, InputStream content) {
        LOG.info("postFile '" + path + "' content...");
        return localFileService.doPostFile(baseDir, path, content);
    }

}
