package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "clusterconfig",
  uniqueConstraints = {@UniqueConstraint(name = "UQ_config_type_tag", columnNames = {"cluster_id", "type_name", "version_tag"}),
    @UniqueConstraint(name = "UQ_config_type_version", columnNames = {"cluster_id", "type_name", "version"})})
@TableGenerator(name = "config_id_generator",
  table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
  , pkColumnValue = "config_id_seq"
  , initialValue = 1
)
@NamedQueries({
    @NamedQuery(
        name = "ClusterConfigEntity.findNextConfigVersion",
        query = "SELECT COALESCE(MAX(clusterConfig.version),0) + 1 as nextVersion FROM ClusterConfigEntity clusterConfig WHERE clusterConfig.type=:configType AND clusterConfig.clusterId=:clusterId"),
    @NamedQuery(
        name = "ClusterConfigEntity.findAllConfigsByStack",
        query = "SELECT clusterConfig FROM ClusterConfigEntity clusterConfig WHERE clusterConfig.clusterId=:clusterId AND clusterConfig.stack=:stack"),
    @NamedQuery(
        name = "ClusterConfigEntity.findLatestConfigsByStack",
        query = "SELECT clusterConfig FROM ClusterConfigEntity clusterConfig WHERE clusterConfig.clusterId = :clusterId AND clusterConfig.stack = :stack AND clusterConfig.selectedTimestamp = (SELECT MAX(clusterConfig2.selectedTimestamp) FROM ClusterConfigEntity clusterConfig2 WHERE clusterConfig2.clusterId=:clusterId AND clusterConfig2.stack=:stack AND clusterConfig2.type = clusterConfig.type)"),
    @NamedQuery(
        name = "ClusterConfigEntity.findLatestConfigsByStackWithTypes",
        query = "SELECT clusterConfig FROM ClusterConfigEntity clusterConfig WHERE clusterConfig.type IN :types AND clusterConfig.clusterId = :clusterId AND clusterConfig.stack = :stack AND clusterConfig.selectedTimestamp = (SELECT MAX(clusterConfig2.selectedTimestamp) FROM ClusterConfigEntity clusterConfig2 WHERE clusterConfig2.clusterId=:clusterId AND clusterConfig2.stack=:stack AND clusterConfig2.type = clusterConfig.type)"),
    @NamedQuery(
        name = "ClusterConfigEntity.findNotMappedClusterConfigsToService",
        query = "SELECT clusterConfig FROM ClusterConfigEntity clusterConfig WHERE clusterConfig.serviceConfigEntities IS EMPTY AND clusterConfig.type != 'cluster-env'"),
    @NamedQuery(
        name = "ClusterConfigEntity.findEnabledConfigsByStack",
        query = "SELECT config FROM ClusterConfigEntity config WHERE config.clusterId = :clusterId AND config.selected = 1 AND config.stack = :stack"),
    @NamedQuery(
        name = "ClusterConfigEntity.findEnabledConfigByType",
        query = "SELECT config FROM ClusterConfigEntity config WHERE config.clusterId = :clusterId AND config.selected = 1 and config.type = :type"),
    @NamedQuery(
        name = "ClusterConfigEntity.findEnabledConfigsByTypes",
        query = "SELECT config FROM ClusterConfigEntity config WHERE config.clusterId = :clusterId AND config.selected = 1 and config.type in :types"),
    @NamedQuery(
        name = "ClusterConfigEntity.findEnabledConfigs",
        query = "SELECT config FROM ClusterConfigEntity config WHERE config.clusterId = :clusterId AND config.selected = 1") })

public class ClusterConfigEntity {

  @Id
  @Column(name = "config_id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "config_id_generator")
  private Long configId;

  @Column(name = "cluster_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long clusterId;

  @Column(name = "type_name")
  private String type;

  @Column(name = "version")
  private Long version;

  @Column(name = "version_tag")
  private String tag;

  @Column(name = "selected", insertable = true, updatable = true, nullable = false)
  private int selected = 0;

  @Basic(fetch = FetchType.LAZY)
  @Column(name = "config_data", nullable = false, insertable = true)
  @Lob
  private String configJson;

  @Basic(fetch = FetchType.LAZY)
  @Column(name = "config_attributes", nullable = true, insertable = true)
  @Lob
  private String configAttributesJson;

  @Column(name = "create_timestamp", nullable = false, insertable = true, updatable = false)
  private long timestamp;

  /**
   * The most recent time that this configuration was marked as
   * {@link #selected}. This is useful when configruations are being reverted
   * since a reversion does not create a new instance. Another configuration may
   * technically be newer via its creation date ({@link #timestamp}), however
   * that does not indicate it was the most recently enabled configuration.
   */
  @Column(name = "selected_timestamp", nullable = false, insertable = true, updatable = true)
  private long selectedTimestamp = 0;

  @ManyToOne
  @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false)
  private ClusterEntity clusterEntity;

  @OneToMany(mappedBy = "clusterConfigEntity")
  private Collection<ConfigGroupConfigMappingEntity> configGroupConfigMappingEntities;

  @ManyToMany(mappedBy = "clusterConfigEntities")
  private Collection<ServiceConfigEntity> serviceConfigEntities;

  @Column(name = "unmapped", nullable = false, insertable = true, updatable = true)
  private short unmapped = 0;

  /**
   * Unidirectional one-to-one association to {@link StackEntity}
   */
  @OneToOne
  @JoinColumn(name = "stack_id", unique = false, nullable = false, insertable = true, updatable = true)
  private StackEntity stack;

}
