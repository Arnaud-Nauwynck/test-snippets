package fr.an.tests.testaggridbig.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class StreetNameAndPrefixTypes {
    public List<PrefixStreetTypeDTO> prefixStreetTypes;
    public List<StreetNameDTO> streetNames;
}
