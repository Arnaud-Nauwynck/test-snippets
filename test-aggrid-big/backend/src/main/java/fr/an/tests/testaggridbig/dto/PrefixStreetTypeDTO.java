package fr.an.tests.testaggridbig.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrefixStreetTypeDTO {

    public int id;
    public String streetType;

    @JsonIgnore
    public String renormStreetType;

    public String addressPrefix;

    @JsonIgnore
    public String streetTypeAndPrefix;
    @JsonIgnore
    public String renormStreetTypeAndPrefix;

    @JsonIgnore
    public PrefixStreetTypeDTO renormEntry;

}
