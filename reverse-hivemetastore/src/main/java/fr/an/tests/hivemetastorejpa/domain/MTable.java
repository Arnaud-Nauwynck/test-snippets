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

@Entity
@Table(name = "TBLS")
@Data
public class MTable {

	@Id
	@Column(name = "TBL_ID", nullable = false)
	private long id;
	
    @Column(name = "TBL_NAME", length = 256)
	private String tableName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DB_ID")
	private MDatabase database;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SD_ID")
	private MStorageDescriptor sd;
	
    @Column(name = "OWNER", length = 767)
	private String owner;
    
    @Column(name = "OWNER_TYPE", length = 10)
	private String ownerType;

	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;

    @Column(name = "LAST_ACCESS_TIME", nullable = false)
    private int lastAccessTime;

    @Column(name = "RETENTION", nullable = false)
    private int retention;

    @OneToMany(mappedBy = "tblId")
	private List<MPartitionKey> partitionKeys;

    @Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MPartitionKeyPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int tblId;
		private int integerIdx;
    }
    
	@Entity
	@Table(name = "PARTITION_KEYS")
	@IdClass(MPartitionKeyPK.class)
	@Data
	public static class MPartitionKey { // TODO MFieldSchema
		@Id
		@Column(name = "TBL_ID", nullable = false)
		private int tblId;

		@Id
		@Column(name = "INTEGER_IDX", nullable = false)
		private int integerIdx;

		@Column(name = "PKEY_COMMENT", length = 4000)
		private String comment;

		@Column(name = "PKEY_NAME", length = 128, nullable = false)
		private String name;

		@Column(name = "PKEY_TYPE", length = 767, nullable = false)
		private String type;
	}

	
	
	// private Map<String, String> parameters;
	@OneToMany(mappedBy = "tblId")
	private List<TableParameter> parameters;

    @Data
	@NoArgsConstructor @AllArgsConstructor
	public static class TableParameterPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int tblId;
		private String paramName;
    }

	@Entity
	@Table(name = "TABLE_PARAMS")
	@IdClass(TableParameterPK.class)
	@Data
	public static class TableParameter {
		@Id
		@Column(name = "TBL_ID", nullable = false)
		private int tblId;

		@Id
		@Column(name = "PARAM_NAME", length = 256, nullable = false)
		private String paramName;

		@Column(name = "PARAM_VALUE")
		private Clob paramValue;
	}


	
    @Column(name = "VIEW_ORIGINAL_TEXT")
	private Clob viewOriginalText;
    
    @Column(name = "VIEW_EXPANDED_TEXT")
	private Clob viewExpandedText;
	
    @Column(name = "IS_REWRITE_ENABLED", nullable = false)
	private boolean rewriteEnabled;

	@Column(name = "TBL_TYPE", length = 128)
	private String tableType;

	@Column(name = "WRITE_ID")
	private long writeId;

	@SuppressWarnings("serial")
	@Data
	public static class PK implements Serializable {

		public long id;

	}

}
