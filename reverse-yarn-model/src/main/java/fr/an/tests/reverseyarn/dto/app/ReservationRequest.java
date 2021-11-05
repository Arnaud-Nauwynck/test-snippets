package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class ReservationRequest {
	Resource capability;
    int numContainers;
	int concurrency;
	long duration;
}
