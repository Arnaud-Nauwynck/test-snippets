package fr.an.google.hashcode.fr2015.model;

import java.util.ArrayList;
import java.util.List;

import fr.an.google.hashcode.fr2015.model.occ.ServerOcc;

public final class RemainingServerTypeCountPerServerType {

    public final ServerType serverType;

    public int count;
    
    // unused in solving ... for display only
    public List<ServerOcc> serverOccs = new ArrayList<ServerOcc>();
    
    // ------------------------------------------------------------------------

    public RemainingServerTypeCountPerServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public void initAddServerOcc(ServerOcc serverOcc) {
        count++;
        serverOccs.add(serverOcc);
    }
    
    // ------------------------------------------------------------------------
    
    
}
