package fr.an.google.hashcode.fr2015;

import java.io.FileNotFoundException;

import fr.an.google.hashcode.fr2015.io.DataCenterReader;
import fr.an.google.hashcode.fr2015.io.DataCenterWriter;
import fr.an.google.hashcode.fr2015.model.DataCenter;
import fr.an.google.hashcode.fr2015.model.Row;

public class Main {

    public DataCenter datacenter;

    public static void main(String[] args) throws FileNotFoundException {
        new Main().run("dc.in", "dc.output");
    }
    
    public void run(String intputFile, String outputFile) {
        datacenter = new DataCenterReader().readResource(intputFile);
        
        // TODO compute optimal assignement ...
        
        
        
        for (Row row : datacenter.getRows()) {
            System.out.println(row.getSlotsAsString());
        }

        DataCenterWriter writer = new DataCenterWriter();
        writer.write(datacenter, outputFile);

    }


}
