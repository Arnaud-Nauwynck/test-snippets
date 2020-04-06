package fr.an.tests.reverseyarn.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReservationRequests {
	
	List<ReservationRequest> reservationResources;
    ReservationRequestInterpreter type;

}
