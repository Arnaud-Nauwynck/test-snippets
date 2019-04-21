package fr.an.test.ambarijpa;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * The {@link AlertGroupEntity} class manages the logical groupings of
 * {@link AlertDefinitionEntity}s in order to easily define what alerts an
 * {@link AlertTargetEntity} should be notified on.
 */
@Entity
@Table(
    name = "alert_group",
    uniqueConstraints = @UniqueConstraint(columnNames = { "cluster_id", "group_name" }))
@TableGenerator(
    name = "alert_group_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "alert_group_id_seq",
    initialValue = 0)
@NamedQueries({
    @NamedQuery(
        name = "AlertGroupEntity.findAll",
        query = "SELECT alertGroup FROM AlertGroupEntity alertGroup"),
    @NamedQuery(
        name = "AlertGroupEntity.findAllInCluster",
        query = "SELECT alertGroup FROM AlertGroupEntity alertGroup WHERE alertGroup.clusterId = :clusterId"),
    @NamedQuery(
        name = "AlertGroupEntity.findByNameInCluster",
        query = "SELECT alertGroup FROM AlertGroupEntity alertGroup WHERE alertGroup.groupName = :groupName AND alertGroup.clusterId = :clusterId"),
    @NamedQuery(
        name = "AlertGroupEntity.findByAssociatedDefinition",
        query = "SELECT alertGroup FROM AlertGroupEntity alertGroup WHERE :alertDefinition MEMBER OF alertGroup.alertDefinitions"),
    @NamedQuery(
        name = "AlertGroupEntity.findServiceDefaultGroup",
        query = "SELECT alertGroup FROM AlertGroupEntity alertGroup WHERE alertGroup.clusterId = :clusterId AND alertGroup.serviceName = :serviceName AND alertGroup.isDefault = 1"),
    @NamedQuery(
        name = "AlertGroupEntity.findByIds",
        query = "SELECT alertGroup FROM AlertGroupEntity alertGroup WHERE alertGroup.groupId IN :groupIds") })
public class AlertGroupEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "alert_group_id_generator")
  @Column(name = "group_id", nullable = false, updatable = false)
  private Long groupId;

  @Column(name = "cluster_id", nullable = false)
  private Long clusterId;

  @Column(name = "group_name", nullable = false, length = 255)
  private String groupName;

  @Column(name = "is_default", nullable = false)
  private Integer isDefault = Integer.valueOf(0);

  @Column(name = "service_name", nullable = true, length = 255)
  private String serviceName;

  /**
   * Bi-directional many-to-many association to {@link AlertDefinitionEntity}
   */
  @ManyToMany(cascade = CascadeType.MERGE)
  @JoinTable(
      name = "alert_grouping",
      joinColumns = { @JoinColumn(name = "group_id", nullable = false) },
      inverseJoinColumns = { @JoinColumn(name = "definition_id", nullable = false) })
  private Set<AlertDefinitionEntity> alertDefinitions;

  /**
   * Unidirectional many-to-many association to {@link AlertTargetEntity}
   */
  @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
  @JoinTable(
      name = "alert_group_target",
      joinColumns = { @JoinColumn(name = "group_id", nullable = false) },
      inverseJoinColumns = { @JoinColumn(name = "target_id", nullable = false) })
  private Set<AlertTargetEntity> alertTargets;

}
