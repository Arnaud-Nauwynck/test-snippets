package fr.an.google.hashcode.fr2015.solver;

import fr.an.google.hashcode.fr2015.model.DataCenter;
import fr.an.google.hashcode.fr2015.model.Group;
import fr.an.google.hashcode.fr2015.model.RemainingServerTypeCountPerServerType;
import fr.an.google.hashcode.fr2015.model.Row;
import fr.an.google.hashcode.fr2015.model.Row.RowRemainingSpacesCountPerSpaceType;
import fr.an.google.hashcode.fr2015.model.ServerType;

public class RecursiveSolver {

    private DataCenter dc;
    
    private final RemainingServerTypeCountPerServerType[] stCounts;
    private final ServerType[] serverTypes;
    private final Group[] groups;
    private final Row[] rows;
    
    private int displayFreqModulo = 10 * 1000*1000; 
    private int displayFreqIndex = displayFreqModulo;
    
    public RecursiveSolver(DataCenter dc) {
        this.dc = dc;
        serverTypes = dc.getServerTypes();
        stCounts = dc.getCountPerServerTypesArray();
        groups = dc.getGroups();
        rows = dc.getRows();
    }

    public void solve() {
        recursiveSolveFromServerType(0);
    }
    
    /**
     * current indexes for (recursive) cartesian product ....
     *  => cf call to DataCenter.assign(ServerType st, int stCount, Group group, Row row, int spaceSize)
     */
    public static class CurrIndexes {
        int stId;
        int stCount;
        
        int groupId;
        int rowId;
        
        int rowSpaceSize;
    }
    
    public void recursiveSolveFromServerType(int fromStId) {
        for(int stId = fromStId; stId < stCounts.length; stId++) {
            ServerType st = serverTypes[stId];
                    
            for(int groupId = 0; groupId < groups.length; groupId++) {
                Group group = groups[groupId];
                // if (group...) break;
                
                for(int rowId = 0; rowId < rows.length; rowId++) {
                    Row row = rows[rowId];

                    if (row.getUnassignedTotalRemainingSize() < st.size) continue;
                    
                    
                    // int remainStCount = stCounts[stId].getCount();
                    for (int stCount = stCounts[stId].getCount()-1; stCount > 0; stCount--) { // TODO using stCount=0 may be possible for some inneficient server type! ... => recurse without assignement!
                        
                        
                        // iterate over linked-list, by decreasing spaceType size...
                        // instead of sparse array  row.getSpaceTypeCountPerSizeArray()
                        RowRemainingSpacesCountPerSpaceType spaceTypeCount = row.getSortedSpaceTypeCountListHead().getNextRemaining();
                        RowRemainingSpacesCountPerSpaceType spaceTypeCountTail = row.getSortedSpaceTypeCountListTail();
                        for(; spaceTypeCount != spaceTypeCountTail; spaceTypeCount = spaceTypeCount.getNextRemaining()) {
                            int spaceSize = spaceTypeCount.rowSpaceType.size;

                            // test if assignement is possible
                            if (spaceTypeCount.getRemainingCount() < st.size) continue;
                            
                            
                            // do assign if possible ... 
                            if (stCount != 0) {
                                dc.addAssignIncr(st, stCount, group, row, spaceSize);
                                
                                // compute if solution is better than bestSoFar
                                // TODO ...
                                
                                
                                if (--displayFreqIndex == 0) {
                                    displayFreqIndex = displayFreqModulo;
                                    System.out.print('.');
                                    // System.out.println("st:" + stId);
                                }
                            }
                            
                            // *** recurse ****
                            if (stId+1 < stCounts.length) {
                                recursiveSolveFromServerType(stId+1);
                            }
                            
                            // undo assign after recurse done
                            if (stCount != 0) {
                                dc.removeAssignIncr(st, stCount, group, row, spaceSize);
                            }
                        }
                        
                    } // for stCount
                    
                    // also handle case stCount = 0 ... for some inneficient server type! ... => recurse without assignement!
                    // *** recurse ****
                    if (stId+1 < stCounts.length) {
                        recursiveSolveFromServerType(stId+1);
                    }
                    
                }
                
            }
        }
    }
    
    
}
