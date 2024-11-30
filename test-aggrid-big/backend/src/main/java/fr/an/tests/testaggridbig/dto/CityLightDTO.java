package fr.an.tests.testaggridbig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class CityLightDTO implements Serializable {

    public int id;

    @JsonProperty("n")
    public String name;

    @JsonProperty("z")
    public String zipCode;

    @JsonProperty("c")
    public int addressCount;

}
