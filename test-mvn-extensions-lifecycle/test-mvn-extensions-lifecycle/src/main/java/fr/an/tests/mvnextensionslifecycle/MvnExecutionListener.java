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
        DisplayStackUtils.message("ExecutionListener", "projectDiscoveryStarted" );
    }

    @Override
    public void sessionStarted( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "sessionStarted" );
    }

    @Override
    public void sessionEnded( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "sessionEnded" );
    }

    @Override
    public void projectSkipped( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "projectSkipped" );
    }

    @Override
    public void projectStarted( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "projectStarted" );
    }

    @Override
    public void projectSucceeded( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "projectSucceeded" );
    }

    @Override
    public void projectFailed( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "projectFailed" );
    }

    @Override
    public void mojoSkipped( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "mojoSkipped" );
    }

    @Override
    public void mojoStarted( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "mojoStarted" );
    }

    @Override
    public void mojoSucceeded( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "mojoSucceeded" );
    }

    @Override
    public void mojoFailed( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "mojoFailed" );
    }

    @Override
    public void forkStarted( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "forkStarted" );
    }

    @Override
    public void forkSucceeded( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "forkSucceeded" );
    }

    @Override
    public void forkFailed( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "forkFailed" );
    }

    @Override
    public void forkedProjectStarted( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "forkedProjectStarted" );
    }

    @Override
    public void forkedProjectSucceeded( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "forkedProjectSucceeded" );
    }

    @Override
    public void forkedProjectFailed( ExecutionEvent event )
    {
        DisplayStackUtils.message("ExecutionListener", "forkedProjectFailed" );
    }

}
