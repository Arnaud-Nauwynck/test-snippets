package fr.an.tests.mvnextensionslifecycle;

import org.apache.maven.execution.MojoExecutionEvent;
import org.apache.maven.execution.MojoExecutionListener;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;

@Component( role = MojoExecutionListener.class, hint = "test-lifecycle" )
public class MvnMojoExecutionListener
    implements MojoExecutionListener
{
    public void beforeMojoExecution( MojoExecutionEvent event )
        throws MojoExecutionException
    {
        DisplayStackUtils.message("MojoExecutionListener", "beforeMojoExecution" );
    }

    public void afterMojoExecutionSuccess( MojoExecutionEvent event )
        throws MojoExecutionException
    {
        DisplayStackUtils.message("MojoExecutionListener", "afterMojoExecutionSuccess" );
    }

    public void afterExecutionFailure( MojoExecutionEvent event )
    {
        DisplayStackUtils.message("MojoExecutionListener", "afterExecutionFailure" );
    }

}
