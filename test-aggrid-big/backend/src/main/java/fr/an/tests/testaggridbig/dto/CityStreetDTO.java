package fr.an.tests.testaggridbig.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CityStreetDTO {

    public final int id;
    public final int cityId;
    public final int streetNameId;

    public CoordSetDTO coordSet = new CoordSetDTO();

    public int addressCount;

    public CityStreetLightDTO toLightDTO() {
        return new CityStreetLightDTO(id, cityId, streetNameId, addressCount, coordSet.getAvgCoord());
    }

}
