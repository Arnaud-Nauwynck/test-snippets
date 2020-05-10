package fr.an.tests.hivemetastorejpa;

import java.sql.Clob;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name = "SDS")
@Data
public class MStorageDescriptor {

	@Id
	@Column(name = "SD_ID")
	private int sdId;
	
	@ManyToOne
	@Column(name = "CD_ID")
	private MColumnDescriptor cd;
	
	@Column(name = "LOCATION", length = 4000)
	private String location;
	
	@Column(name = "INPUT_FORMAT", length = 4000)
	private String inputFormat;

	@Column(name = "OUTPUT_FORMAT", length = 4000)
	private String outputFormat;
	
	@Column(name = "IS_COMPRESSED", nullable = false)
	private boolean isCompressed = false;
	
	@Column(name = "NUM_BUCKETS", nullable = false)
	private int numBuckets = 1;
	
    @ManyToOne
    @Column(name = "SERDE_ID")
	private MSerDeInfo serDeInfo;
	
	
	// private List<String> bucketCols;
	private List<BucketingColumn> bucketCols;
	
	@Entity
	@Table(name = "BUCKETING_COLS",
			uniqueConstraints = @UniqueConstraint(name = "BUCKETING_COLS_pkey", columnNames = "SD_ID, INTEGER_IDX")
			)
	@Data
	public static class BucketingColumn {
	    @Id 
	    @Column(name = "SD_ID", nullable = false)
	    private int sdId;

	    @Id 
	    @Column(name = "INTEGER_IDX", nullable = false)
	    private int integerIdx;
	    
	    @Column(name = "BUCKET_COL_NAME", length = 256)
	    private String bucketColName; 
	}

	
	@OneToMany(mappedBy = "sdId")
	private List<MOrder> sortCols;
	
	@Entity
	@Table(name = "SORT_COLS")
	@Data
	public static class MOrder {
		@Id
		@Column(name = "SD_ID", nullable = false)
		private int sdId;

		@Id
		@Column(name = "INTEGER_IDX", nullable = false)
		private int integerIdx;
		
		@Column(name = "COLUMN_NAME", length = 767)
		private String columnName;
		
		@Column(name = "ORDER", nullable = false)
		private int order;
	}

	
	// private Map<String, String> parameters;
	private List<StorageDescriptorParameter> parameters;
	
	@Entity
	@Table(name = "SD_PARAMS")
	@Data
	public static class StorageDescriptorParameter {
		@Id
		@Column(name = "SD_ID", nullable = false)
		private int sdId;

		@Id
		@Column(name = "PARAM_KEY", length = 256, nullable = false)
		private String paramKey;

		@Column(name = "PARAM_VALUE")
		private Clob paramValue;

	}

	// private List<String> skewedColNames;
	@OneToMany(mappedBy = "sdId")
	private List<SkewedColName> skewedColNames;
	
	@Entity
	@Table(name = "SKEWED_COL_NAMES")
	@Data
	public static class SkewedColName {

		@Id
		@Column(name = "SD_ID")
		private int sdId;

		@Id
		@Column(name = "INTEGER_IDX", nullable = false)
		private int integerIdx;

		@Column(name = "SKEWED_COL_NAME", length = 256)
		private String skewedColName;
	
	}
	

	
	// private List<MStringList> skewedColValues;
	@OneToMany(mappedBy = "sdId")
	private List<SkewedColValue> skewedColValues;
	
	@Entity
	@Table(name = "SKEWED_VALUES")
	@Data
	public static class SkewedColValue {
	
		@Id
		@Column(name = "SD_ID_OID")
		private int sdId;
	
		@Id
		@Column(name = "INTEGER_IDX", nullable = false)
		private int integerIdx;
	
		@ManyToOne
		@Column(name = "STRING_LIST_ID_EID", length = 256)
		private MStringList stringList;
	
	}
	
	// private Map<MStringList, String> skewedColValueLocationMaps;
	//	@ManyToMany
//	@JoinTable(
//		name="SKEWED_COL_VALUE_LOC_MAP",
//		joinColumns={@JoinColumn(name="sdId", referencedColumnName="sdId")},
//		inverseJoinColumns={@JoinColumn(name="sdId", referencedColumnName="sdId")})
//	@MapKey(name = "stringList")
	private List<SkewedColValueLocMap> skewedColValueLocationMaps;

	@Entity
	@Table(name = "SKEWED_COL_VALUE_LOC_MAP")
	@Data
	public static class SkewedColValueLocMap {
		@Id
		@Column(name = "SD_ID")
		private int sdId;
		
		@Id
		@ManyToOne
		@Column(name = "STRING_LIST_ID_KID", nullable = false)
		private MStringList stringList;
		
		@Column(name = "LOCATION", length = 4000)
		private String location;
		
	}
	
	
    @Column(name = "IS_STOREDASSUBDIRECTORIES", nullable = false)
	private boolean isStoredAsSubDirectories;

}
