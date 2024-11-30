package fr.an.tests.testaggridbig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CoordDTO {

    @JsonProperty("lg")
    public float longitude;

    @JsonProperty("la")
    public float lattitude;

    //---------------------------------------------------------------------------------------------

    public void set(CoordDTO src) {
        this.longitude = src.longitude;
        this.lattitude = src.lattitude;
    }

}
