package fr.an.tests.testaggridbig.dto;

import java.util.HashMap;
import java.util.Map;

public class StreetNameDTO {

    public int id;
    public String name;

    public int countAddress;
    public Map<String,Integer> countByCityZipCode = new HashMap<>();
}
