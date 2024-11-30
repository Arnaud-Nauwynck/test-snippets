package fr.an.tests.testaggridbig.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityDTO implements Serializable {

    public int id;
    public String name;
    public String zipCode;

    public CoordSetDTO coordSet = new CoordSetDTO();

    public int addressCount;

    @JsonIgnore
    public Map<Integer,CityStreetDTO> streetById = new HashMap<>();

    @JsonIgnore
    public List<CityStreetDTO> streets = new ArrayList<CityStreetDTO>();

    public CityLightDTO toCityLightDTO() {
        return new CityLightDTO(id, name, zipCode, addressCount);
    }
}
