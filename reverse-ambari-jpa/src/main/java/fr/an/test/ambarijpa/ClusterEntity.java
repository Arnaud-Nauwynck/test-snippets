package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.SecurityType;
import fr.an.test.ambarijpa.state.State;

@Table(name = "clusters")
@NamedQueries({
    @NamedQuery(name = "clusterByName", query =
        "SELECT cluster " +
            "FROM ClusterEntity cluster " +
            "WHERE cluster.clusterName=:clusterName"),
    @NamedQuery(name = "allClusters", query =
        "SELECT clusters " +
            "FROM ClusterEntity clusters"),
    @NamedQuery(name = "clusterByResourceId", query =
        "SELECT cluster " +
            "FROM ClusterEntity cluster " +
            "WHERE cluster.resource.id=:resourceId")
})
@Entity
@TableGenerator(name = "cluster_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
    , pkColumnValue = "cluster_id_seq"
    , initialValue = 1
)
public class ClusterEntity {

  @Id
  @Column(name = "cluster_id", nullable = false, insertable = true, updatable = true)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "cluster_id_generator")
  private Long clusterId;

  @Basic
  @Column(name = "cluster_name", nullable = false, insertable = true,
      updatable = true, unique = true, length = 100)
  private String clusterName;

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "provisioning_state", insertable = true, updatable = true)
  private State provisioningState = State.INIT;

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "security_type", nullable = false, insertable = true, updatable = true)
  private SecurityType securityType = SecurityType.NONE;

  @Basic
  @Column(name = "desired_cluster_state", insertable = true, updatable = true)
  private String desiredClusterState = "";

  @Basic
  @Column(name = "cluster_info", insertable = true, updatable = true)
  private String clusterInfo = "";

  /**
   * Unidirectional one-to-one association to {@link StackEntity}
   */
  @OneToOne
  @JoinColumn(name = "desired_stack_id", unique = false, nullable = false, insertable = true, updatable = true)
  private StackEntity desiredStack;

  @OneToMany(mappedBy = "clusterEntity")
  private Collection<ClusterServiceEntity> clusterServiceEntities;

  @OneToOne(mappedBy = "clusterEntity", cascade = CascadeType.REMOVE)
  private ClusterStateEntity clusterStateEntity;

  @ManyToMany(mappedBy = "clusterEntities")
  private Collection<HostEntity> hostEntities;

  @OneToMany(mappedBy = "clusterEntity", cascade = CascadeType.ALL)
  private Collection<ClusterConfigEntity> configEntities;

  @OneToMany(mappedBy = "clusterEntity", cascade = CascadeType.ALL)
  private Collection<ConfigGroupEntity> configGroupEntities;

  @OneToMany(mappedBy = "clusterEntity", cascade = CascadeType.ALL)
  private Collection<RequestScheduleEntity> requestScheduleEntities;

  @OneToMany(mappedBy = "clusterEntity", cascade = CascadeType.REMOVE)
  private Collection<ServiceConfigEntity> serviceConfigEntities;

  @OneToMany(mappedBy = "clusterEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private Collection<AlertDefinitionEntity> alertDefinitionEntities;

  @OneToMany(mappedBy = "clusterEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private Collection<WidgetEntity> widgetEntities;

  @OneToMany(mappedBy = "clusterEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private Collection<WidgetLayoutEntity> widgetLayoutEntities;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumns({
      @JoinColumn(name = "resource_id", referencedColumnName = "resource_id", nullable = false)
  })
  private ResourceEntity resource;

  @Basic
  @Column(name = "upgrade_id", nullable = true, insertable = false, updatable = false)
  private Long upgradeId;

  /**
   * {@code null} when there is no upgrade/downgrade in progress.
   */
  @OneToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(
      name = "upgrade_id",
      referencedColumnName = "upgrade_id",
      nullable = true,
      insertable = false,
      updatable = true)
  private UpgradeEntity upgradeEntity = null;

}
