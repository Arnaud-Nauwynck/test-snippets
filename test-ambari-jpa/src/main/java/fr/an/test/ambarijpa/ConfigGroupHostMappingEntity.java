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

import fr.an.test.ambarijpa.ConfigGroupHostMappingEntity.ConfigGroupHostMappingEntityPK;

@IdClass(ConfigGroupHostMappingEntityPK.class)
@Entity
@Table(name = "configgrouphostmapping")
@NamedQueries({
  @NamedQuery(name = "groupsByHost", query =
  "SELECT confighosts FROM ConfigGroupHostMappingEntity confighosts " +
    "WHERE confighosts.hostEntity.hostName=:hostname"),
  @NamedQuery(name = "hostsByGroup", query =
  "SELECT confighosts FROM ConfigGroupHostMappingEntity confighosts " +
    "WHERE confighosts.configGroupId=:groupId")
})
public class ConfigGroupHostMappingEntity {

  @Id
  @Column(name = "config_group_id", nullable = false, insertable = true, updatable = true)
  private Long configGroupId;

  @Id
  @Column(name = "host_id", nullable = false, insertable = true, updatable = true)
  private Long hostId;

  public static class ConfigGroupHostMappingEntityPK implements Serializable {
	  @Id
	  @Column(name = "config_group_id", nullable = false, insertable = true, updatable = true)
	  private Long configGroupId;

	  @Id
	  @Column(name = "host_id", nullable = false, insertable = true, updatable = true)
	  private Long hostId;

  }
  
  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "host_id", referencedColumnName = "host_id", nullable = false, insertable = false, updatable = false) })
  private HostEntity hostEntity;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "config_group_id", referencedColumnName = "group_id", nullable = false, insertable = false, updatable = false) })
  private ConfigGroupEntity configGroupEntity;

}
