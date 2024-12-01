package fr.an.tests.testaggridbig.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CoordSetDTO {

    public CoordDTO minCoord = new CoordDTO();
    public CoordDTO maxCoord = new CoordDTO();

    @JsonIgnore
    public CoordDTO sumCoord = new CoordDTO();
    public int addressCount;

    public CoordDTO avgCoord;
    public CoordDTO getAvgCoord() {
        if (avgCoord == null) {
            float norm = (float) ((addressCount != 0) ? (1.0 / addressCount) : 1.0);
            this.avgCoord = new CoordDTO(sumCoord.longitude * norm, sumCoord.lattitude * norm);
        }
        return avgCoord;
    }

    public void init(CoordDTO coord) {
        minCoord.set(coord);
        maxCoord.set(coord);
    }

    public void addCoord(CoordDTO src) {
        minCoord.longitude = Math.min(src.longitude, minCoord.longitude);
        minCoord.lattitude = Math.min(src.longitude, minCoord.lattitude);
        maxCoord.longitude = Math.max(src.longitude, maxCoord.longitude);
        maxCoord.lattitude = Math.max(src.longitude, maxCoord.lattitude);
        sumCoord.longitude += src.longitude;
        sumCoord.lattitude += src.lattitude;
        addressCount++;
    }

}
