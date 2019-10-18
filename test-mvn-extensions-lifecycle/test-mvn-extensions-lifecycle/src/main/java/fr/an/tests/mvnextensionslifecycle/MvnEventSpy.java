package fr.an.tests.mvnextensionslifecycle;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionEvent.Type;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.component.annotations.Component;

@Component( role = EventSpy.class, hint = "test-lifecycle" )
public class MvnEventSpy extends AbstractEventSpy
{

    @Override
    public void init( Context context )
        throws Exception
    {
        System.out.println( "####### EventSpy.init" );
    }

    @Override
    public void onEvent( Object event )
        throws Exception
    {
        try {
            if (event instanceof org.eclipse.aether.RepositoryEvent) {
                // ARTIFACT_RESOLVING..
                // ARTIFACT_RESOLVED
                return;
            }
            if (event instanceof ExecutionEvent) {
                ExecutionEvent executionEvent = (ExecutionEvent) event;
                Type execType = executionEvent.getType();
//                ProjectDiscoveryStarted,
//                SessionStarted,
//                SessionEnded,
//                ProjectSkipped,
//                ProjectStarted,
//                ProjectSucceeded,
//                ProjectFailed,
//                MojoSkipped,
//                MojoStarted,
//                MojoSucceeded,
//                MojoFailed,
//                ForkStarted,
//                ForkSucceeded,
//                ForkFailed,
//                ForkedProjectStarted,
//                ForkedProjectSucceeded,
//                ForkedProjectFailed,

                MojoExecution mojoExec = executionEvent.getMojoExecution();
                if (mojoExec == null) {
                    System.out.println( "####### EventSpy.onEvent ExecutionEvent " 
                                    + execType
                                    );
                    return;
                }
                System.out.println( "####### EventSpy.onEvent ExecutionEvent " 
                                + execType
                                + " "
                                + mojoExec.getGroupId() + ":" + mojoExec.getArtifactId()
                                + ":" + mojoExec.getGoal()
                                + " (" + mojoExec.getExecutionId() + ")"
                                );
                
                return;
            }
            System.out.println( "####### EventSpy.onEvent " + event.getClass().getName() + " " + event );
        } catch(Exception ex) {
            System.out.println( "####### FAILED EventSpy.onEvent " + ex.getMessage() );
            ex.printStackTrace(System.out);
        }
    }   

    @Override
    public void close()
        throws Exception
    {
        System.out.println( "####### EventSpy.close" );
    }

    
}
