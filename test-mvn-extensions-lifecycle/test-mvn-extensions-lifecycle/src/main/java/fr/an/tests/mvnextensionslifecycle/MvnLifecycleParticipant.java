package fr.an.tests.mvnextensionslifecycle;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

@Component( role = AbstractMavenLifecycleParticipant.class, hint = "test-lifecycle" )
public class MvnLifecycleParticipant
    extends AbstractMavenLifecycleParticipant
{

    @Override
    public void afterSessionStart( MavenSession session )
        throws MavenExecutionException
    {
        System.out.println( "#### afterSessionStart" );
    }

    @Override
    public void afterProjectsRead( MavenSession session )
        throws MavenExecutionException
    {
        System.out.println( "#### afterProjectsRead" );
    }

    
}