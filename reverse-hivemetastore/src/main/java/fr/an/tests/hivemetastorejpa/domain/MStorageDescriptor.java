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
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SDS")
@Data
public class MStorageDescriptor {

	@Id
	@Column(name = "SD_ID")
	private int sdId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CD_ID")
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
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERDE_ID")
	private MSerDeInfo serDeInfo;
	
	@OneToMany(mappedBy = "sdId")
	// private List<String> bucketCols;
	private List<BucketingColumn> bucketCols;
	
	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class BucketingColumnPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int sdId;
		private int integerIdx;
	}
	
	@Entity
	@Table(name = "BUCKETING_COLS",
			uniqueConstraints = @UniqueConstraint(name = "BUCKETING_COLS_pkey", 
				columnNames = { "SD_ID", "INTEGER_IDX" })
			)
	@IdClass(BucketingColumnPK.class)
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
	
	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MOrderPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int sdId;
		private int integerIdx;
	}
	
	@Entity
	@Table(name = "SORT_COLS")
	@IdClass(MOrderPK.class)
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

	
	@OneToMany(mappedBy = "sdId")
	// private Map<String, String> parameters;
	private List<StorageDescriptorParameter> parameters;

	@Data @NoArgsConstructor @AllArgsConstructor
	public static class StorageDescriptorParameterPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int sdId;
		private String paramKey;
	}
	
	@Entity
	@Table(name = "SD_PARAMS")
	@IdClass(StorageDescriptorParameterPK.class)
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

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class SkewedColNamePK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int sdId;
		private int integerIdx;
	}

	@Entity
	@Table(name = "SKEWED_COL_NAMES")
	@IdClass(SkewedColNamePK.class)
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

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class SkewedColValuePK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int sdId;
		private int integerIdx;
	}
	
	@Entity
	@Table(name = "SKEWED_VALUES")
	@IdClass(SkewedColValuePK.class)
	@Data
	public static class SkewedColValue {
	
		@Id
		@Column(name = "SD_ID_OID")
		private int sdId;
	
		@Id
		@Column(name = "INTEGER_IDX", nullable = false)
		private int integerIdx;
	
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "STRING_LIST_ID_EID")
		private MStringList stringList;
	
	}
	
	// private Map<MStringList, String> skewedColValueLocationMaps;
	@OneToMany(mappedBy = "sdId")
//	@JoinTable(
//		name="SKEWED_COL_VALUE_LOC_MAP",
//		joinColumns={@JoinColumn(name="sdId", referencedColumnName="sdId")},
//		inverseJoinColumns={@JoinColumn(name="sdId", referencedColumnName="sdId")})
//	@MapKey(name = "stringList")
	private List<SkewedColValueLocMap> skewedColValueLocationMaps;

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class SkewedColValueLocMapPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int sdId;
		private int stringList;
	}

	
	@Entity
	@Table(name = "SKEWED_COL_VALUE_LOC_MAP")
	@IdClass(SkewedColValueLocMapPK.class)
	@Data
	public static class SkewedColValueLocMap {
		@Id
		@Column(name = "SD_ID")
		private int sdId;
		
		@Id
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "STRING_LIST_ID_KID", nullable = false)
		private MStringList stringList;
		
		@Column(name = "LOCATION", length = 4000)
		private String location;
		
	}
	
	
    @Column(name = "IS_STOREDASSUBDIRECTORIES", nullable = false)
	private boolean isStoredAsSubDirectories;

}
