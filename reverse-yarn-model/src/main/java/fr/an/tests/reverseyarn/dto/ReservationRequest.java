package fr.an.tests.reverseyarn.dto;

import lombok.Data;

@Data
public class ReservationRequest {
	Resource capability;
    int numContainers;
	int concurrency;
	long duration;
}
