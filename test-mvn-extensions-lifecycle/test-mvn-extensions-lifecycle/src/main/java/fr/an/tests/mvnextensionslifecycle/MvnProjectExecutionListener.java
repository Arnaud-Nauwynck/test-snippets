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
        System.out.println( "#### ProjectExecutionListener.beforeProjectExecution" );
    }

    public void beforeProjectLifecycleExecution( ProjectExecutionEvent event )
        throws LifecycleExecutionException
    {
        System.out.println( "#### ProjectExecutionListener.beforeProjectLifecycleExecution" );

    }

    public void afterProjectExecutionSuccess( ProjectExecutionEvent event )
        throws LifecycleExecutionException
    {
        System.out.println( "#### ProjectExecutionListener.afterProjectExecutionSuccess" );

    }

    public void afterProjectExecutionFailure( ProjectExecutionEvent event )
    {
        System.out.println( "#### ProjectExecutionListener.afterProjectExecutionFailure" );
    }

}
