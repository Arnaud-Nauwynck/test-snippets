package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "NOTIFICATION_SEQUENCE")
@Data
public class MNotificationNextId {

	@Id
	@Column(name = "NNI_ID", nullable = false)
	private int nniId; 

	@Column(name = "NEXT_EVENT_ID", nullable = false)
	private long nextEventId;

}
