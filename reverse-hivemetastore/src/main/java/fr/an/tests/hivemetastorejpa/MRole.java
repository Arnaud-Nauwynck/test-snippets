package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ROLES")
@Data
public class MRole {

	@Id
	@Column(name = "ROLE_ID")
	private int roleId;

	@Column(name = "ROLE_NAME", length = 128)
	private String roleName;

	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;

	@Column(name = "OWNER_NAME", length = 128)
	private String ownerName;

}
