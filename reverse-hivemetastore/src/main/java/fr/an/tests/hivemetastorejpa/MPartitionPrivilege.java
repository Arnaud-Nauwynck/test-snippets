package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "PART_PRIVS")
@Data
public class MPartitionPrivilege {

	@Id
	@Column(name = "PART_GRANT_ID")
	private int partGrantId;

	@Column(name = "PRINCIPAL_NAME", length = 128)
	private String principalName;

	@Column(name = "PRINCIPAL_TYPE", length = 128)
	private String principalType;

	@ManyToOne
	@Column(name = "PART_ID")
	private MPartition partition;

	@Column(name = "PART_PRIV", length = 128)
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
