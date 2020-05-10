package fr.an.tests.hivemetastorejpa;

import java.sql.Clob;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "SERDES")
@Data
public class MSerDeInfo {

	@Id
	@Column(name = "SERDE_ID", nullable = false)
	private int serdeId;

	@Column(name = "NAME", length = 128)
	private String name;

	@Column(name = "SLIB", length = 4000)
	private String serializationLib;

	// private Map<String, String> parameters;
	@OneToMany(mappedBy = "serdeId")
	private List<SerdeParameter> parameters;

	@Entity
	@Table(name = "SERDE_PARAMS")
	@Data
	public static class SerdeParameter {
		@Id
		@Column(name = "SERDE_ID", nullable = false)
		private int serdeId;

		@Column(name = "PARAM_KEY", length = 256, nullable = false)
		private String paramKey;

		@Column(name = "PARAM_VALUE")
		private Clob paramValue;

	}

	@Column(name = "DESCRIPTION", length = 4000)
	private String description;

	@Column(name = "SERIALIZER_CLASS", length = 4000)
	private String serializerClass;

	@Column(name = "DESERIALIZER_CLASS", length = 4000)
	private String deserializerClass;

	@Column(name = "SERDE_TYPE")
	private int serdeType;

}
