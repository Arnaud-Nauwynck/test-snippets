package fr.an.tests.testaggridbig.rest;

import fr.an.tests.testaggridbig.dto.*;
import fr.an.tests.testaggridbig.service.BalService;
import fr.an.tests.testaggridbig.service.FirstNameService;
import fr.an.tests.testaggridbig.util.LsUtils;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bal")
@OpenAPIDefinition(
        // tags = { @Tag("OpenData-Bal") }
)
@Slf4j
public class BalRestController {

    protected final BalService balService;
    protected final FirstNameService firstNameService;

    public BalRestController(BalService balService, FirstNameService firstNameService) {
        this.balService = balService;
        this.firstNameService = firstNameService;
    }

    @GetMapping("/summary")
    @Operation(summary = "get summary of OpenData BAL")
    public BalSummaryDTO getSummary() {
        return balService.getBalSummary();
    }

    @GetMapping("/prefix-streets-types")
    @Operation(summary = "get prefix street types")
    public List<PrefixStreetTypeDTO> getPrefixStreetTypes() {
        return balService.getPrefixStreetTypes();
    }

    @GetMapping("/streets-names")
    @Operation(summary = "get street names")
    public List<StreetNameDTO> getStreetNames() {
        return balService.getStreetNames();
    }

    @GetMapping("/streets-names-and-prefix-types")
    @Operation(summary = "get street names and prefix types")
    public StreetNameAndPrefixTypes getStreetNameAndPrefixTypes() {
        val prefixTypes = balService.getPrefixStreetTypes();
        val streetNames = balService.getStreetNames();
        return new StreetNameAndPrefixTypes(prefixTypes, streetNames);
    }

    @GetMapping("/cities-full")
    @Operation(summary = "get cities (full info)")
    public List<CityDTO> getCitiesFull() {
        return balService.getCities();
    }

    @GetMapping("/cities")
    @Operation(summary = "get cities")
    public List<CityLightDTO> getCitiesLight() {
        return LsUtils.map(balService.getCities(), CityDTO::toCityLightDTO);
    }

    @GetMapping("/streets")
    @Operation(summary = "get streets")
    public List<CityStreetLightDTO> getStreetsLight() {
        return LsUtils.map(balService.getStreets(), CityStreetDTO::toLightDTO);
    }


    @GetMapping("/addresses")
    @Operation(summary = "get addresses")
    public List<AddressDTO> getAddresses(
            @RequestParam("limit") int limit
    ) {
        List<AddressDTO> tmpres = balService.getAddresses();
        if (limit != 0) {
            tmpres = tmpres.subList(0, Math.min(limit, tmpres.size()));
        }
        return tmpres;
    }

    @GetMapping("/addresses-range")
    @Operation(summary = "get addresses, range [from,to(")
    public List<AddressDTO> getAddressesRange(
            @RequestParam("from") int from,
            @RequestParam("to") int to
    ) {
        List<AddressDTO> tmpres = balService.getAddresses();
        int len = tmpres.size();
        int from2 = Math.min(len, from);
        int to2 = Math.max(from2, Math.min(len, to));
        return tmpres.subList(from2, to2);
    }

    @GetMapping("/first-names")
    @Operation(summary = "get french first names")
    public List<FirstnameDTO> getFirstNames() {
        return firstNameService.getFirstNames();
    }

}
