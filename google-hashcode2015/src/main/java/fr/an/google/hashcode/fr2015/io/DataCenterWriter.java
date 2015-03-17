package fr.an.google.hashcode.fr2015.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import fr.an.google.hashcode.fr2015.model.DataCenter;
import fr.an.google.hashcode.fr2015.model.Group;
import fr.an.google.hashcode.fr2015.model.Row;
import fr.an.google.hashcode.fr2015.model.occ.ServerOcc;
import fr.an.google.hashcode.fr2015.model.occ.SlotOcc;

public class DataCenterWriter {
	
	public void write(DataCenter datacenter, String fileName) {
	    PrintStream out = null;
        try {
            out = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            write(datacenter, out);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to print file " + fileName, ex);
        } finally {
            if (out!=null){
                try {
                    out.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
	}
	
	public void write(DataCenter datacenter, PrintStream out){
		ServerOcc[] servers =  datacenter.getServerOccs();
		for(ServerOcc server : servers){
			int serverId = server.getId();
			SlotOcc slot = server.getSlot();
			if (slot == null){
				out.println("x");
			} else {
				Row row =  slot.getRow();
				Group group = server.getGroup();
				out.println(row.getId() + " " + slot.getId() + " " + group.getId());
				System.out.println(String.format("Serveur %s placé rangée %s, emplacement %s et affecté au groupe %s",""+serverId, ""+row.getId(),""+slot.getId(),""+group.getId()) );
			}		
		}
	}
	
}
