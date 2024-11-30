package fr.an.tests.testaggridbig.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BalSummaryDTO {

    public int cityCount;
    public int streetNameCount;
    public int cityStreetCount;
    public int addressCount;

    @Override
    public String toString() {
        return "{" +
                "cityCount=" + cityCount +
                ", streetNameCount=" + streetNameCount +
                ", cityStreetCount=" + cityStreetCount +
                ", addressCount=" + addressCount +
                '}';
    }
}
