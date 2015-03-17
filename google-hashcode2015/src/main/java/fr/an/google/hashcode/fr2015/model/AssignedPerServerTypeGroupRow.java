package fr.an.google.hashcode.fr2015.model;


public class AssignedPerServerTypeGroupRow {
//        public final Group group;
//        public final Row row;
        public final ServerType serverType;
        
        private int count;
        private int capacity;
        
        public AssignedPerServerTypeGroupRow(ServerType serverType) {
            this.serverType = serverType;
        }
        
        public void assignIncr(int delta) {
            count += delta;
            capacity += delta * serverType.capacity;
        }
        
        public int getCount() {
            return count;
        }
        public int getCapacity() {
            return capacity;
        }
        
    }