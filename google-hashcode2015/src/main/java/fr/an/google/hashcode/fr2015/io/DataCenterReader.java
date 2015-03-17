package fr.an.google.hashcode.fr2015.io;

import java.io.InputStream;
import java.util.Scanner;

import fr.an.google.hashcode.fr2015.model.DataCenter;
import fr.an.google.hashcode.fr2015.model.DataCenterBuilder;
import fr.an.google.hashcode.fr2015.model.DataCenterBuilder.ServerInfo;
import fr.an.google.hashcode.fr2015.model.DataCenterBuilder.UnavailableSlotInfo;

public class DataCenterReader {

    
    
	public DataCenterReader() {
	}

    public DataCenter readResource(String resourceName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return readResource(cl, resourceName);
    }
    
    public DataCenter readResource(ClassLoader cl, String resourceName) {
        DataCenter res;
        InputStream is = cl.getResourceAsStream(resourceName);
        try {
            res = read(is);
        } finally {
            try { is.close(); } catch(Exception ex) {}
        }
        return res;
    }

    
	public DataCenter read(InputStream is) {
		Scanner scn = new Scanner(is);
		
		DataCenterBuilder b = new DataCenterBuilder();
		b.rowsCount = scn.nextInt();
		b.rowSize = scn.nextInt();
		int unavailableSlotsCount = scn.nextInt();
		b.groupsCount = scn.nextInt();
		b.serversCount = scn.nextInt();
		
		// System.out.println(String.format("r:%d  s:%d  u:%d  p:%d  m:%d  ", b.rowsCount, b.rowSize, unavailableSlotsCount, b.groupsCount, b.serversCount));
		scn.nextLine(); // ?
		
		for (int i = 0; i < unavailableSlotsCount; i++) {
			int rowId = scn.nextInt();
			int slotId = scn.nextInt();
			b.addUnavailableSlotInfo(new UnavailableSlotInfo(rowId, slotId));
		}
		
		for (int i = 0; i < b.serversCount; i++) {
			int size = scn.nextInt();
			int capacity = scn.nextInt();
			b.addServerData(new ServerInfo(size, capacity));
		}
        
		DataCenter datacenter = b.build();
		
		scn.close(); // => also close InputStream!
		
		return datacenter;
	}
	
	
}
