package fr.an.tests.testjson.rest;

import java.io.File;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.an.tests.testjson.impl.LocalFileService;

/**
 * example Rest controller class combining jax-rs (http REST) + JGit
 * 
 * usage of jax-rs (htt REST) => see corresponding simpler class LocalFileRestcontroller
 * usage of JGit => cf  https://git-scm.com/book/en/v2/Embedding-Git-in-your-Applications-JGit
 * 
 *
 */
@Service
@Path("/git") // => "/cxf/git"
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class LocalJGitFileRest {
    
    private static final Logger LOG = LoggerFactory.getLogger(LocalJGitFileRest.class);
    
    @Inject
    private LocalFileService localFileService;
    
    @Value("${baseDir:src/test/testJGitBaseDir}")
    private File baseDir;

    private Repository jgitRepo;
    private Git jGit;
    
    @PostConstruct
    public void init() {
        try {
            File gitDir = new File(baseDir, ".git");
            if (! baseDir.exists()) {
                baseDir.mkdirs();
                jgitRepo = FileRepositoryBuilder.create(gitDir);
                jgitRepo.create();
            }
                
            jgitRepo = new FileRepositoryBuilder()
                    .setGitDir(gitDir)
                    .setWorkTree(baseDir)
                    .build();
            jGit = new Git(jgitRepo);
        } catch(Exception ex) {
            LOG.error("Failed to init jgit repo", ex);
        }
    }
    
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
        Response res = localFileService.doPostFile(baseDir, path, content);
        if (res.getStatus() != Status.OK.getStatusCode()) {
            return res;
        }

        // do commit in local jgit...
        try {
            AddCommand addCmd = jGit.add();
            addCmd.addFilepattern(path);
            addCmd.call();
            
            jGit.commit().setMessage("postFile '" + path + "' => commit").call();
        } catch(Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to commit in git, ex:" + ex.getMessage()).build();
        }
        return Response.ok().build();
    }
    
    @GET
    @Path("/{path}/log")
    @Produces(MediaType.TEXT_PLAIN)
    public Response gitLogFile(@PathParam("path") String path) {
        LOG.info("gitLogFile '" + path + "'");
        StringBuilder res = new StringBuilder();
        try {
            Iterable<RevCommit> revCommits = jGit.log().addPath(path).call();
            for(RevCommit revCommit : revCommits) {
                res.append(revCommit + "\n");
                res.append(revCommit.getFullMessage() + "\n");
                
                res.append("\n");
            }
        } catch(Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to query git log, ex:" + ex.getMessage()).build();
        }
        return Response.ok().entity(res.toString()).build();
    }
    
    
}
