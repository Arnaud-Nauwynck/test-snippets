package fr.an.tests.hivemetastorejpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Storage Class representing the Hive MDatabase in a rdbms
 *
 */
@Data
@Entity
@Table(name="DBS")
public class MDatabase {

	@Id
	@Column(name = "DB_ID")
	private int id;
	
	@Column(name = "NAME", length = 128)
	private String name;
	
	@Column(name="DB_LOCATION_URI", nullable = false, length = 4000)
	private String locationUri;
	
	@Column(name="DB_MANAGED_LOCATION_URI", length = 4000)
	private String managedLocationUri;

	@Column(name = "DESC", length = 4000)
	private String description;

	// TODO
	// private Map<String, String> parameters;
	@OneToMany(mappedBy = "db")
	private List<DbParameter> parameters;
	
	@Entity
	@Data
	@Table(name = "DATABASE_PARAMS")
	public static class DbParameter {
		@Id
		@ManyToOne
		@JoinColumn(name = "DB_ID", nullable = false)
		private MDatabase db;
		
		@Column(name = "PARAM_KEY", length = 180, nullable = false)
		private String paramKey;
		
		@Column(name = "PARAM_VALUE", length = 4000)
		private String paramValue;
	}

	
	@Column(name = "OWNER_NAME", length = 128)
	private String ownerName;
	
	@Column(name = "OWNER_TYPE", length = 10)
	private String ownerType;
	
	@Column(name = "CTLG_NAME", length = 256)
	private String catalogName = "hive";
	
	@Column(name = "CREATE_TIME") // bigint
	private int createTime;

}
