package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "FUNC_RU")
@Data
public class MResourceUri {

	@Id
	@JoinColumn(name = "FUNC_ID", nullable = false)
	private MFunction func;

	@Id
	@Column(name = "INTEGER_IDX", nullable = false)
	private int integerIdx;

	@Column(name = "RESOURCE_TYPE", nullable = false)
	private int resourceType;

	@Column(name = "RESOURCE_URI", length = 4000)
	private String resourceUri;
	
}
