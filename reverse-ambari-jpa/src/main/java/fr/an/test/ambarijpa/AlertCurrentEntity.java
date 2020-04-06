package fr.an.test.ambarijpa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.AlertFirmness;
import fr.an.test.ambarijpa.state.AlertState;
import fr.an.test.ambarijpa.state.MaintenanceState;

/**
 * The {@link AlertCurrentEntity} class represents the most recently received an
 * alert data for a given instance. This class always has an associated matching
 * {@link AlertHistoryEntity} that defines the actual data of the alert.
 * <p/>
 * There will only ever be a single entity for each given
 * {@link AlertDefinitionEntity}.
 */
@Entity
@Table(name = "alert_current")
@TableGenerator(name = "alert_current_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "alert_current_id_seq", initialValue = 0)
@NamedQueries({
  @NamedQuery(name = "AlertCurrentEntity.findAll", query = "SELECT alert FROM AlertCurrentEntity alert"),
  @NamedQuery(name = "AlertCurrentEntity.findByCluster", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertHistory.clusterId = :clusterId"),
  @NamedQuery(name = "AlertCurrentEntity.findByDefinitionId", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertDefinition.definitionId = :definitionId"),
  @NamedQuery(name = "AlertCurrentEntity.findByService", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertHistory.clusterId = :clusterId AND alert.alertHistory.serviceName = :serviceName AND alert.alertHistory.alertDefinition.scope IN :inlist"),
  @NamedQuery(name = "AlertCurrentEntity.findByHostAndName", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertHistory.clusterId = :clusterId AND alert.alertHistory.alertDefinition.definitionName = :definitionName AND alert.alertHistory.hostName = :hostName"),
  @NamedQuery(name = "AlertCurrentEntity.findByNameAndNoHost", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertHistory.clusterId = :clusterId AND alert.alertHistory.alertDefinition.definitionName = :definitionName AND alert.alertHistory.hostName IS NULL"),
  @NamedQuery(name = "AlertCurrentEntity.findByHostComponent", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertHistory.serviceName = :serviceName AND alert.alertHistory.componentName = :componentName AND alert.alertHistory.hostName = :hostName"),
  @NamedQuery(name = "AlertCurrentEntity.findByHost", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertHistory.hostName = :hostName"),
  @NamedQuery(name = "AlertCurrentEntity.findByServiceName", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertHistory.serviceName = :serviceName"),
  @NamedQuery(name = "AlertCurrentEntity.findDisabled", query = "SELECT alert FROM AlertCurrentEntity alert WHERE alert.alertDefinition.enabled = 0"),
  @NamedQuery(name = "AlertCurrentEntity.removeByHistoryId", query = "DELETE FROM AlertCurrentEntity alert WHERE alert.historyId = :historyId"),
  // The remove queries can be handled by a simpler JPQL query,
  // however, MySQL gtid enforce policy gets violated due to creation and
  // deletion of TEMP table in the same transaction
  @NamedQuery(name = "AlertCurrentEntity.removeByHistoryIds", query = "DELETE FROM AlertCurrentEntity alert WHERE alert.historyId IN :historyIds"),
  @NamedQuery(name = "AlertCurrentEntity.removeByDefinitionId", query = "DELETE FROM AlertCurrentEntity alert WHERE alert.definitionId = :definitionId")
})
public class AlertCurrentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "alert_current_id_generator")
  @Column(name = "alert_id", nullable = false, updatable = false)
  private Long alertId;

  @Column(name = "history_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long historyId;

  @Column(name = "definition_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long definitionId;

  @Column(name = "latest_timestamp", nullable = false)
  private Long latestTimestamp;

  @Column(name = "maintenance_state", length = 255)
  @Enumerated(value = EnumType.STRING)
  private MaintenanceState maintenanceState = MaintenanceState.OFF;

  @Column(name = "original_timestamp", nullable = false)
  private Long originalTimestamp;

  @Lob
  @Column(name = "latest_text")
  private String latestText = null;

  /**
   * The number of occurrences of this alert in its current state. States which
   * are not {@link AlertState#OK} are aggregated such that transitioning
   * between these states should not reset this value. For example, if an alert
   * bounces between {@link AlertState#WARNING} and {@link AlertState#CRITICAL},
   * then it will not reset this value.
   *
   */
  @Column(name="occurrences", nullable=false)
  private Long occurrences = Long.valueOf(1);

  @Column(name = "firmness", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private AlertFirmness firmness = AlertFirmness.HARD;

  /**
   * Unidirectional one-to-one association to {@link AlertHistoryEntity}
   */
  @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
  @JoinColumn(name = "history_id", unique = true, nullable = false)
  private AlertHistoryEntity alertHistory;

  /**
   * Unidirectional one-to-one association to {@link AlertDefinitionEntity}
   */
  @OneToOne
  @JoinColumn(name = "definition_id", unique = false, nullable = false)
  private AlertDefinitionEntity alertDefinition;

}
