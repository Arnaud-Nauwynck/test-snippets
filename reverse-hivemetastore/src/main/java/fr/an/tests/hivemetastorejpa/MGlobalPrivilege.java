package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * User global level privileges
 */
@Entity
@Table(name = "GLOBAL_PRIVS")
@Data
public class MGlobalPrivilege {

	@Id
	@Column(name = "USER_GRANT_ID")
	private int userGrantId;

	// principal name, can be a user, group, or role
	@Column(name = "PRINCIPAL_NAME", length = 128)
	private String principalName;

	@Column(name = "PRINCIPAL_TYPE", length = 128)
	private String principalType;

	@Column(name = "USER_PRIV", length = 128)
	private String privilege;

	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;

	@Column(name = "GRANTOR", length = 128)
	private String grantor;

	@Column(name = "GRANTOR_TYPE", length = 128)
	private String grantorType;

	@Column(name = "GRANT_OPTION", nullable = false)
	private boolean grantOption;

	@Column(name = "AUTHORIZER", length = 128)
	private String authorizer;

}
