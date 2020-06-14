package fr.an.tests.hivemetastorejpa.domain;

import java.io.Serializable;
import java.sql.Clob;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the creation metadata of a materialization. It includes the
 * database and table name for the materialization, the set of tables that it
 * uses, the valid transaction list when it was created, and the
 * creation/rebuild time.
 */
@Entity
@Table(name = "MV_CREATION_METADATA")
@Data
public class MCreationMetadata {

	@Id
	@Column(name = "MV_CREATION_METADATA_ID")
	private int mvCreationMetadataId;

	@Column(name = "CAT_NAME", length = 256, nullable = false)
	private String catalogName;

	@Column(name = "DB_NAME", length = 128, nullable = false)
	private String dbName;

	@Column(name = "TBL_NAME", length = 256, nullable = false)
	private String tblName;

	// private Set<MTable> tables;
	@OneToMany(mappedBy = "mvCreationMetadataId")
	private List<MCreationTablesUsed> tables;

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MCreationTablesUsedPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int mvCreationMetadataId;
		private int table;
	}

	@Entity
	@Table(name = "MV_TABLES_USED")
	@IdClass(MCreationTablesUsedPK.class)
	@Data
	public static class MCreationTablesUsed {
		@Id
		@Column(name = "MV_CREATION_METADATA_ID")
		private int mvCreationMetadataId;

		@Id
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "TBL_ID")
		private MTable table;
	}



	
	@Column(name = "TXN_LIST")
	private Clob txnList;

	@Column(name = "MATERIALIZATION_TIME", nullable = false)
	private long materializationTime;

}
