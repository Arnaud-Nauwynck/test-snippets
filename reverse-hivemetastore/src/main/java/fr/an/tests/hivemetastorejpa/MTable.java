package fr.an.tests.hivemetastorejpa;

import java.io.Serializable;
import java.sql.Clob;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TBLS")
public class MTable {

	@Id
	@Column(name = "TBL_ID", nullable = false)
	private long id;
	
    @Column(name = "TBL_NAME", length = 256)
	private String tableName;

    @Column(name = "DB_ID")
	@ManyToOne
	private MDatabase database;

    @ManyToOne
    @Column(name = "SD_ID")
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

	private List<MFieldSchema> partitionKeys;

	@Data
	@Table(name = "PARTITION_KEYS")
	@Entity
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
	@Column()
	private List<TableParameter> parameters;

	@Entity
	@Table(name = "TABLE_PARAMS")
	@Data
	public static class TableParameter {
		@Id
		@Column(name = "TBL_ID", nullable = false)
		private int tblId;

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
