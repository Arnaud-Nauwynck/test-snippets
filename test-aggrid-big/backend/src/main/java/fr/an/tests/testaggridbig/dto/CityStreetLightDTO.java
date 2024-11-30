package fr.an.tests.testaggridbig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CityStreetLightDTO {

    public final int id;

    @JsonProperty("c")
    public final int cityId;

    @JsonProperty("s")
    public final int streetId;

    @JsonProperty("add#")
    public final int addressCount;
}
