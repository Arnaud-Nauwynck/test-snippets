package fr.an.tests.reverseyarn.dto.app;

import lombok.Value;

@Value
public class ReservationId {

	protected final long clusterTimestamp;
	protected final long id;

	@Override
	public String toString() {
		return "reservation_" + clusterTimestamp + "_" + id; // FastNumberFormat.format(sb, id, 4);
	}

}
