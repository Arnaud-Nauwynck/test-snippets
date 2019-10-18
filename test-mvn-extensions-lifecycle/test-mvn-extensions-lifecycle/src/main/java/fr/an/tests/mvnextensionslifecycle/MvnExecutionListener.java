package fr.an.tests.mvnextensionslifecycle;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionListener;
import org.codehaus.plexus.component.annotations.Component;

@Component( role = ExecutionListener.class, hint = "test-lifecycle" )
public class MvnExecutionListener implements ExecutionListener 
{

    @Override
    public void projectDiscoveryStarted( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.projectDiscoveryStarted" );
    }

    @Override
    public void sessionStarted( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.sessionStarted" );
    }

    @Override
    public void sessionEnded( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.sessionEnded" );
    }

    @Override
    public void projectSkipped( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.projectSkipped" );
    }

    @Override
    public void projectStarted( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.projectStarted" );
    }

    @Override
    public void projectSucceeded( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.projectSucceeded" );
    }

    @Override
    public void projectFailed( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.projectFailed" );
    }

    @Override
    public void mojoSkipped( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.mojoSkipped" );
    }

    @Override
    public void mojoStarted( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.mojoStarted" );
    }

    @Override
    public void mojoSucceeded( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.mojoSucceeded" );
    }

    @Override
    public void mojoFailed( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.mojoFailed" );
    }

    @Override
    public void forkStarted( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.forkStarted" );
    }

    @Override
    public void forkSucceeded( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.forkSucceeded" );
    }

    @Override
    public void forkFailed( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.forkFailed" );
    }

    @Override
    public void forkedProjectStarted( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.forkedProjectStarted" );
    }

    @Override
    public void forkedProjectSucceeded( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.forkedProjectSucceeded" );
    }

    @Override
    public void forkedProjectFailed( ExecutionEvent event )
    {
        System.out.println( "### ExecutionListener.forkedProjectFailed" );
    }

}
