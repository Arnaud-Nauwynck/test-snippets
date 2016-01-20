package fr.an.testsjdk8mojo;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo( name = "helloinvoke")
public class HelloInvokeMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;
	
	
	@Parameter(property = "invokeMessage", defaultValue="Invoke Hello World")
	private String invokeMessage;

	
	
	public void execute() throws MojoExecutionException {

		executeMojo(
		    plugin(
		        groupId("org.apache.maven.plugins"),
		        artifactId("testjdk8mojo-maven-plugin"),
		        version("1.0-SNAPSHOT")
		    ),
		    goal("hello"),
		    configuration(
		        element(name("message"), invokeMessage)
		    ),
		    executionEnvironment(
		        mavenProject,
		        mavenSession,
		        pluginManager
		    )
		);
	}
	
}
