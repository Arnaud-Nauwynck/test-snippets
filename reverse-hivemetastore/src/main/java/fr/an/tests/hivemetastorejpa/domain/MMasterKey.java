package fr.an.tests.hivemetastorejpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "MASTER_KEYS")
@Data
public class MMasterKey {

	@Id
	@Column(name = "KEY_ID")
	private int keyId;

	@Column(name = "MASTER_KEY", length = 767)
	private String masterKey;

}
