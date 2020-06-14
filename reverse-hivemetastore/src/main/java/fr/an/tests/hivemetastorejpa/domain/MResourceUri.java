package fr.an.tests.hivemetastorejpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.an.tests.hivemetastorejpa.domain.MResourceUri.MResourceUriPK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FUNC_RU")
@IdClass(MResourceUriPK.class)
@Data
public class MResourceUri {

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MResourceUriPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int func;
		private int integerIdx;
	}
	
	@Id
	@ManyToOne
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
