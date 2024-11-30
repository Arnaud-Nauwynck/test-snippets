package fr.an.tests.testaggridbig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    @JsonProperty("c")
    public int cityId;

    @JsonProperty("s")
    public int streetId;

    @JsonProperty("n")
    public int num;

    @JsonProperty("ns")
    public String numSuffix;

    @JsonProperty("coord")
    public CoordDTO coord;

}
