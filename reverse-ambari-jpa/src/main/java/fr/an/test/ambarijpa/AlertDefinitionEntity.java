package fr.an.test.ambarijpa;

import java.util.Set;

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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import fr.an.test.ambarijpa.state.Scope;
import fr.an.test.ambarijpa.state.SourceType;

/**
 * The {@link AlertDefinitionEntity} class is used to model an alert that needs
 * to run in the system. Each received alert from an agent will essentially be
 * an instance of this template.
 */
@Entity
@Table(name = "alert_definition", uniqueConstraints = @UniqueConstraint(columnNames = {
  "cluster_id", "definition_name"}))
@TableGenerator(name = "alert_definition_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "alert_definition_id_seq", initialValue = 0)
@NamedQueries({
  @NamedQuery(name = "AlertDefinitionEntity.findAll", query = "SELECT ad FROM AlertDefinitionEntity ad"),
  @NamedQuery(name = "AlertDefinitionEntity.findAllInCluster", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.clusterId = :clusterId"),
  @NamedQuery(name = "AlertDefinitionEntity.findAllEnabledInCluster", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.clusterId = :clusterId AND ad.enabled = 1"),
  @NamedQuery(name = "AlertDefinitionEntity.findByName", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.definitionName = :definitionName AND ad.clusterId = :clusterId",
    hints = {
      @QueryHint(name = "eclipselink.query-results-cache", value = "true"),
      @QueryHint(name = "eclipselink.query-results-cache.ignore-null", value = "true"),
      @QueryHint(name = "eclipselink.query-results-cache.size", value = "5000")
    }),
  @NamedQuery(name = "AlertDefinitionEntity.findByService", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.serviceName = :serviceName AND ad.clusterId = :clusterId"),
  @NamedQuery(name = "AlertDefinitionEntity.findByServiceAndComponent", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.serviceName = :serviceName AND ad.componentName = :componentName AND ad.clusterId = :clusterId"),
  @NamedQuery(name = "AlertDefinitionEntity.findByServiceMaster", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.serviceName IN :services AND ad.scope = :scope AND ad.clusterId = :clusterId AND ad.componentName IS NULL" +
      " AND ad.sourceType <> org.apache.ambari.server.state.alert.SourceType.AGGREGATE"),
  @NamedQuery(name = "AlertDefinitionEntity.findByIds", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.definitionId IN :definitionIds"),
  @NamedQuery(name = "AlertDefinitionEntity.findBySourceType", query = "SELECT ad FROM AlertDefinitionEntity ad WHERE ad.clusterId = :clusterId AND ad.sourceType = :sourceType")})
public class AlertDefinitionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "alert_definition_id_generator")
  @Column(name = "definition_id", nullable = false, updatable = false)
  private Long definitionId;

  @Lob
  @Basic
  @Column(name = "alert_source", nullable = false, length = 32672)
  private String source;

  @Column(name = "cluster_id", nullable = false)
  private Long clusterId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", insertable = false, updatable = false)
  private ClusterEntity clusterEntity;

  @Column(name = "component_name", length = 255)
  private String componentName;

  @Column(name = "definition_name", nullable = false, length = 255)
  private String definitionName;

  @Column(name = "label", nullable = true, length = 255)
  private String label;

  @Column(name = "help_url", nullable = true, length = 512)
  private String helpURL;

  @Lob
  @Basic
  @Column(name = "description", nullable = true, length = 32672)
  private String description;

  @Column(name = "scope", length = 255)
  @Enumerated(value = EnumType.STRING)
  private Scope scope;

  @Column(nullable = false)
  private Integer enabled = Integer.valueOf(1);

  @Column(nullable = false, length = 64)
  private String hash;

  @Column(name = "schedule_interval", nullable = false)
  private Integer scheduleInterval;

  @Column(name = "service_name", nullable = false, length = 255)
  private String serviceName;

  @Column(name = "source_type", nullable = false, length = 255)
  @Enumerated(value = EnumType.STRING)
  private SourceType sourceType;

  @Column(name = "ignore_host", nullable = false)
  private Integer ignoreHost = Integer.valueOf(0);

  /**
   * Indicates how many sequential alerts must be received for a non-OK state
   * change to be considered correct. This value is meant to eliminate
   * false-positive notifications on alerts which are flaky.
   */
  @Column(name = "repeat_tolerance", nullable = false)
  private Integer repeatTolerance = Integer.valueOf(1);

  /**
   * If {@code 1}, then the value of {@link #repeatTolerance} is used to
   * override the global alert tolerance value.
   */
  @Column(name = "repeat_tolerance_enabled", nullable = false)
  private Short repeatToleranceEnabled = Short.valueOf((short) 0);

  /**
   * Bi-directional many-to-many association to {@link AlertGroupEntity}
   */
  @ManyToMany(mappedBy = "alertDefinitions", cascade = {CascadeType.PERSIST,
    CascadeType.MERGE, CascadeType.REFRESH})
  private Set<AlertGroupEntity> alertGroups;

}
