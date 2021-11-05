package fr.an.tests.reverseyarn.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class ReservationRequests {
	
	List<ReservationRequest> reservationResources;
    ReservationRequestInterpreter type;

}
