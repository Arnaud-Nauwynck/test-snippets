package fr.an.test.ambarijpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import fr.an.test.ambarijpa.ConfigGroupConfigMappingEntity.ConfigGroupConfigMappingEntityPK;

@Entity
@Table(name = "confgroupclusterconfigmapping")
@IdClass(ConfigGroupConfigMappingEntityPK.class)
@NamedQueries({
  @NamedQuery(name = "configsByGroup", query =
  "SELECT configs FROM ConfigGroupConfigMappingEntity configs " +
    "WHERE configs.configGroupId=:groupId")
})
public class ConfigGroupConfigMappingEntity {
  @Id
  @Column(name = "config_group_id", nullable = false, insertable = true, updatable = true)
  private Long configGroupId;

  @Id
  @Column(name = "cluster_id", nullable = false, insertable = true, updatable = false)
  private Long clusterId;

  @Id
  @Column(name = "config_type", nullable = false, insertable = true, updatable = false)
  private String configType;

  public static class ConfigGroupConfigMappingEntityPK implements Serializable {
		@Id
		@Column(name = "config_group_id", nullable = false, insertable = true, updatable = true)
	  private Long configGroupId;
		@Id
		@Column(name = "cluster_id", nullable = false, insertable = true, updatable = true, length = 10)
	  private Long clusterId;
		@Id
		@Column(name = "config_type", nullable = false, insertable = true, updatable = true)
	  private String configType;
	}

  
  @Column(name = "version_tag", nullable = false, insertable = true, updatable = false)
  private String versionTag;

  @Column(name = "create_timestamp", nullable = false, insertable = true, updatable = true)
  private Long timestamp;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false, insertable = false, updatable = false),
    @JoinColumn(name = "config_type", referencedColumnName = "type_name", nullable = false, insertable = false, updatable = false),
    @JoinColumn(name = "version_tag", referencedColumnName = "version_tag", nullable = false, insertable = false, updatable = false)
  })
  private ClusterConfigEntity clusterConfigEntity;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "config_group_id", referencedColumnName = "group_id", nullable = false, insertable = false, updatable = false)})
  private ConfigGroupEntity configGroupEntity;

}
