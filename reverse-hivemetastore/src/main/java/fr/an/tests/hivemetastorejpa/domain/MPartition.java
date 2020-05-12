package fr.an.tests.hivemetastorejpa;

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
@Entity
@Table(name = "PARTITIONS")
public class MPartition {

	@Id
	@Column(name = "PART_ID")
	private int partId;

	@Column(name = "PART_NAME", length = 767)
	private String partitionName; // partitionname ==> (key=value/)*(key=value)

	@ManyToOne()
	@Column(name = "TBL_ID")
	private MTable table;

	// private List<String> values;
	@OneToMany(mappedBy = "partId")
	private List<PartitionKeyValue> values;

	@Entity
	@Data
	@Table(name = "PARTITION_KEY_VALS")
	public static class PartitionKeyValue {
		@Id
		@Column(name = "PART_ID", nullable = false)
		private int partId;

		@Id
	    @Column(name = "INTEGER_IDX", nullable = false)
	    private int integerIdx;

		@Column(name = "PART_KEY_VAL", length = 256)
	    private String partKeyValue;
		
	}
	
	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;

	@Column(name = "LAST_ACCESS_TIME", nullable = false)
	private int lastAccessTime;

	@ManyToOne()
	@Column(name = "SD_ID")
	private MStorageDescriptor sd;

//	private Map<String, String> parameters;
	private List<PartitionParameter> parameters;

	@Entity
	@Table(name = "PARTITION_PARAMS")
	@Data
	public static class PartitionParameter {
		@Id
		@Column(name = "PART_ID", nullable = false)
		private int partId;

		@Id
		@Column(name = "PARAM_KEY", length = 256, nullable = false)
	    private String paramKey;

		@Column(name = "PARAM_VALUE")
	    private Clob paramValue;
		
	}

	
	@Column(name = "WRITE_ID")
	private long writeId;

}
