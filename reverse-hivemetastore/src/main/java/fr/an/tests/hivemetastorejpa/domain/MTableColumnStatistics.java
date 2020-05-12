package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * MTableColumnStatistics - Represents Hive's Column Statistics Description. The
 * fields in this class with the exception of table are persisted in the
 * metastore. In case of table, tbl_id is persisted in its place.
 *
 */
@Entity
@Table(name = "TAB_COL_STATS")
@Data
public class MTableColumnStatistics {

	@Id
	@Column(name = "CS_ID", nullable = false)
	private int csId;

	@ManyToOne
	@Column(name = "TBL_ID", nullable = false)
	private MTable table;

	@Column(name = "CAT_NAME", length = 256)
	private String catName;

	@Column(name = "DB_NAME", length = 128)
	private String dbName;

	@Column(name = "TABLE_NAME", length = 256)
	private String tableName;

	@Column(name = "COLUMN_NAME", length = 767)
	private String colName;

	@Column(name = "COLUMN_TYPE", length = 128)
	private String colType;

	@Column(name = "ENGINE", length = 128, nullable = false)
	private String engine;

	@Column(name = "LONG_LOW_VALUE")
	private Long longLowValue;
	@Column(name = "LONG_HIGH_VALUE")
	private Long longHighValue;

	@Column(name = "DOUBLE_LOW_VALUE")
	private Double doubleLowValue;
	@Column(name = "DOUBLE_HIGH_VALUE")
	private Double doubleHighValue;

	@Column(name = "BIG_DECIMAL_LOW_VALUE", length = 4000)
	private String decimalLowValue;
	@Column(name = "BIG_DECIMAL_HIGH_VALUE", length = 4000)
	private String decimalHighValue;

	@Column(name = "NUM_NULLS", nullable = false)
	private Long numNulls;

	@Column(name = "NUM_DISTINCTS")
	private Long numDVs;

	@Column(name = "BIT_VECTOR")
	private byte[] bitVector;

	@Column(name = "AVG_COL_LEN")
	private Double avgColLen;

	@Column(name = "MAX_COL_LEN")
	private Long maxColLen;

	@Column(name = "NUM_TRUES")
	private Long numTrues;

	@Column(name = "NUM_FALSES")
	private Long numFalses;

	@Column(name = "LAST_ANALYZED", nullable = false)
	private long lastAnalyzed;

}
