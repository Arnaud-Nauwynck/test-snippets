package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class ReservationDefinition {

	long arrival;
	long deadline;
	ReservationRequests reservationRequests;
	String name;
    String recurrenceExpression;
    Priority priority;
}
