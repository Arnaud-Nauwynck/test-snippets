package fr.an.google.hashcode.fr2015.io;

import java.io.PrintStream;

import fr.an.google.hashcode.fr2015.model.DataCenter;
import fr.an.google.hashcode.fr2015.model.RemainingServerTypeCountPerServerType;
import fr.an.google.hashcode.fr2015.model.ServerType;

public class DataCenterAssignementsDumper {

    protected PrintStream out;
    protected boolean dumpRemaining;
    protected boolean dumpAssigned;
    
    
    public DataCenterAssignementsDumper(PrintStream out, boolean dumpRemaining, boolean dumpAssigned) {
        this.out = out;
        this.dumpRemaining = dumpRemaining;
        this.dumpAssigned = dumpAssigned;
    }


    public void dump(DataCenter src) {
        out.println("DataCenter.countPerServerTypes");
        for(RemainingServerTypeCountPerServerType stc : src.getCountPerServerTypes().values()) {
            ServerType st = stc.serverType;
            
        }
    }
}
