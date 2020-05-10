package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TBL_PRIVS")
@Data
public class MTablePrivilege {

	@Id
	@Column(name = "TBL_GRANT_ID")
	private int tblGrantId;

	@Column(name = "PRINCIPAL_NAME", length = 128)
	private String principalName;

	@Column(name = "PRINCIPAL_TYPE", length = 128)
	private String principalType;

	@ManyToOne
	@Column(name = "TBL_ID")
	private MTable table;

    @Column(name = "TBL_PRIV", length = 128)
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


