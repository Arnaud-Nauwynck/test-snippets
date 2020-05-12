package fr.an.tests.hivemetastorejpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Represents hive's index definition.
 */
@Deprecated
@Data
@Entity
@Table(name = "IDXS")
public class MIndex {

	@Id
	@Column(name = "INDEX_ID", nullable = false)
	private int indexId;
	
	@Column(name = "INDEX_NAME", length = 128)
	private String indexName;

	@ManyToOne()
	@Column(name = "ORIG_TBL_ID")
	private MTable origTable;
	
	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;
	
	@Column(name = "LAST_ACCESS_TIME", nullable= false)
	private int lastAccessTime;
	
	@OneToMany(mappedBy = "indexId")
//	private Map<String, String> parameters;
	private List<IndexParameter> parameters;
	
	@Entity
	@Data
	@Table(name = "INDEX_PARAMS")
	public static class IndexParameter {
		@Id
		@Column(name = "INDEX_ID", nullable = false) 
		int  indexId;
		@Id
		@Column(name = "PARAM_KEY", length = 256, nullable = false)
		private String paramKey;
		
		@Column(name = "PARAM_VALUE", length = 4000)
		private String paramValue;
	}

	
	@ManyToOne()
	@Column(name = "INDEX_TBL_ID")
	private MTable indexTable;

	@ManyToOne()
	@Column(name = "SD_ID")
	private MStorageDescriptor sd;

	@Column(name = "INDEX_HANDLER_CLASS", length = 4000)
	private String indexHandlerClass;

	@Column(name = "DEFERRED_REBUILD", nullable = false)
	private boolean deferredRebuild;

}
