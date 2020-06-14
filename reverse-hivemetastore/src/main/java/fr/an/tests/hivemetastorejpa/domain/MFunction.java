package fr.an.tests.hivemetastorejpa.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "FUNCS")
@Data
public class MFunction {

	@Id
	@Column(name = "FUNC_ID")
	private int funcId;

	@Column(name = "FUNC_NAME", length = 128)
	private String functionName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DB_ID")
	private MDatabase database;

	@Column(name = "CLASS_NAME", length = 4000)
	private String className;

	@Column(name = "OWNER_NAME", length = 128)
	private String ownerName;

	@Column(name = "OWNER_TYPE", length = 10)
	private String ownerType;

	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;

	@Column(name = "FUNC_TYPE", nullable = false)
	private int functionType;

	@OneToMany(mappedBy = "func")
	private List<MResourceUri> resourceUris;


}
