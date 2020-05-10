package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "DB_PRIVS")
@Data
public class MDBPrivilege {

	@Column(name = "DB_GRANT_ID", nullable = false)
	private int dbGrantId;
	
	@Column(name = "PRINCIPAL_NAME", length = 128)
	private String principalName;

	@Column(name = "PRINCIPAL_TYPE", length = 128)
	private String principalType;

	@ManyToOne
	@Column(name = "DB_ID")
	private MDatabase database;

	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;

	@Column(name = "DB_PRIV", length = 128)
	private String privilege;

	@Column(name = "GRANTOR", length = 128)
	private String grantor;

	@Column(name = "GRANTOR_TYPE", length = 128)
	private String grantorType;

	@Column(name = "GRANT_OPTION", nullable = false)
	private boolean grantOption;

	@Column(name = "AUTHORIZER", length = 128)
	private String authorizer;

}
