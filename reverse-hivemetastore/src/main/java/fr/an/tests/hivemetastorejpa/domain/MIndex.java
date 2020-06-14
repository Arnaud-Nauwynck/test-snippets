package fr.an.tests.hivemetastorejpa.domain;

import java.io.Serializable;
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
 * Represents hive's index definition.
 */
@Deprecated
@Entity
@Table(name = "IDXS")
@Data
public class MIndex {

	@Id
	@Column(name = "INDEX_ID", nullable = false)
	private int indexId;
	
	@Column(name = "INDEX_NAME", length = 128)
	private String indexName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORIG_TBL_ID")
	private MTable origTable;
	
	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;
	
	@Column(name = "LAST_ACCESS_TIME", nullable= false)
	private int lastAccessTime;
	
	@OneToMany(mappedBy = "indexId")
//	private Map<String, String> parameters;
	private List<IndexParameter> parameters;
	
	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MIndexParameterPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int indexId;
		private String paramKey;
	}
	
	@Entity
	@Table(name = "INDEX_PARAMS")
	@IdClass(MIndexParameterPK.class)
	@Data
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

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INDEX_TBL_ID")
	private MTable indexTable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SD_ID")
	private MStorageDescriptor sd;

	@Column(name = "INDEX_HANDLER_CLASS", length = 4000)
	private String indexHandlerClass;

	@Column(name = "DEFERRED_REBUILD", nullable = false)
	private boolean deferredRebuild;

}
