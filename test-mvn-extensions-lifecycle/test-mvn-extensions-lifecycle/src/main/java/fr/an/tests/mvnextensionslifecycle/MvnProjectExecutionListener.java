package fr.an.tests.mvnextensionslifecycle;

import org.apache.maven.execution.ProjectExecutionEvent;
import org.apache.maven.execution.ProjectExecutionListener;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.codehaus.plexus.component.annotations.Component;

@Component( role = ProjectExecutionListener.class, hint = "test-lifecycle" )
public class MvnProjectExecutionListener
    implements ProjectExecutionListener
{
    public void beforeProjectExecution( ProjectExecutionEvent event )
        throws LifecycleExecutionException
    {
        DisplayStackUtils.message("ProjectExecutionListener", "beforeProjectExecution" );
    }

    public void beforeProjectLifecycleExecution( ProjectExecutionEvent event )
        throws LifecycleExecutionException
    {
        DisplayStackUtils.message("ProjectExecutionListener", "beforeProjectLifecycleExecution" );

    }

    public void afterProjectExecutionSuccess( ProjectExecutionEvent event )
        throws LifecycleExecutionException
    {
        DisplayStackUtils.message("ProjectExecutionListener", "afterProjectExecutionSuccess" );

    }

    public void afterProjectExecutionFailure( ProjectExecutionEvent event )
    {
        DisplayStackUtils.message("ProjectExecutionListener", "afterProjectExecutionFailure" );
    }

}
