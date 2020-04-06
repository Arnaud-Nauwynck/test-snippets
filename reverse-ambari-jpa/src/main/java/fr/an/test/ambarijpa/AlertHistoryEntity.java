package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.AlertState;

/**
 * The {@link AlertHistoryEntity} class is an instance of an alert state change
 * that was received. Instances are only stored in the history if there is a
 * state change event. Subsequent alerts that are received for the same state
 * update the timestamps in {@link AlertNoticeEntity} but do not receive a new
 * {@link AlertHistoryEntity}.
 */
@Entity
@Table(name = "alert_history")
@TableGenerator(name = "alert_history_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "alert_history_id_seq", initialValue = 0)
@NamedQueries({
  @NamedQuery(name = "AlertHistoryEntity.findAll", query = "SELECT alertHistory FROM AlertHistoryEntity alertHistory"),
  @NamedQuery(name = "AlertHistoryEntity.findAllInCluster", query = "SELECT alertHistory FROM AlertHistoryEntity alertHistory WHERE alertHistory.clusterId = :clusterId"),
  @NamedQuery(name = "AlertHistoryEntity.findAllInClusterWithState", query = "SELECT alertHistory FROM AlertHistoryEntity alertHistory WHERE alertHistory.clusterId = :clusterId AND alertHistory.alertState IN :alertStates"),
  @NamedQuery(name = "AlertHistoryEntity.findAllInClusterBetweenDates", query = "SELECT alertHistory FROM AlertHistoryEntity alertHistory WHERE alertHistory.clusterId = :clusterId AND alertHistory.alertTimestamp BETWEEN :startDate AND :endDate"),
  @NamedQuery(name = "AlertHistoryEntity.findAllInClusterBeforeDate", query = "SELECT alertHistory FROM AlertHistoryEntity alertHistory WHERE alertHistory.clusterId = :clusterId AND alertHistory.alertTimestamp <= :beforeDate"),
  @NamedQuery(name = "AlertHistoryEntity.findAllIdsInClusterBeforeDate", query = "SELECT alertHistory.alertId FROM AlertHistoryEntity alertHistory WHERE alertHistory.clusterId = :clusterId AND alertHistory.alertTimestamp <= :beforeDate"),
  @NamedQuery(name = "AlertHistoryEntity.findAllInClusterAfterDate", query = "SELECT alertHistory FROM AlertHistoryEntity alertHistory WHERE alertHistory.clusterId = :clusterId AND alertHistory.alertTimestamp >= :afterDate"),
  @NamedQuery(name = "AlertHistoryEntity.removeByDefinitionId", query = "DELETE FROM AlertHistoryEntity alertHistory WHERE alertHistory.alertDefinitionId = :definitionId"),
  @NamedQuery(name = "AlertHistoryEntity.removeInClusterBeforeDate", query = "DELETE FROM AlertHistoryEntity alertHistory WHERE alertHistory.clusterId = :clusterId AND alertHistory.alertTimestamp <= :beforeDate"),
  @NamedQuery(name = "AlertHistoryEntity.findHistoryIdsByDefinitionId", query = "SELECT alertHistory.alertId FROM AlertHistoryEntity alertHistory WHERE alertHistory.alertDefinitionId = :definitionId ORDER BY alertHistory.alertId")
})
public class AlertHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "alert_history_id_generator")
  @Column(name = "alert_id", nullable = false, updatable = false)
  private Long alertId;

  @Column(name = "alert_instance", length = 255)
  private String alertInstance;

  @Column(name = "alert_label", length = 1024)
  private String alertLabel;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "alert_state", nullable = false, length = 255)
  private AlertState alertState;

  @Lob
  @Column(name = "alert_text")
  private String alertText;

  @Column(name = "alert_timestamp", nullable = false)
  private Long alertTimestamp;

  @Column(name = "cluster_id", nullable = false)
  private Long clusterId;

  @Column(name = "component_name", length = 255)
  private String componentName;

  @Column(name = "host_name", length = 255)
  private String hostName;

  @Column(name = "service_name", nullable = false, length = 255)
  private String serviceName;


  @ManyToOne
  @JoinColumn(name = "alert_definition_id", nullable = false)
  private AlertDefinitionEntity alertDefinition;

  @Column(name = "alert_definition_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long alertDefinitionId;

}
