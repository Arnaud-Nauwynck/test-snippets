package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.NotificationState;

/**
 * The {@link AlertNoticeEntity} class represents the need to dispatch a
 * notification to an {@link AlertTargetEntity}. There are three
 * {@link NotificationState}s that a single notice can exist in. These instances
 * are persisted indefinitely for historical reference.
 *
 */
@Entity
@Table(name = "alert_notice")
@TableGenerator(name = "alert_notice_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "alert_notice_id_seq", initialValue = 0)
@NamedQueries({
  @NamedQuery(name = "AlertNoticeEntity.findAll", query = "SELECT notice FROM AlertNoticeEntity notice"),
  @NamedQuery(name = "AlertNoticeEntity.findByState", query = "SELECT notice FROM AlertNoticeEntity notice WHERE notice.notifyState = :notifyState  ORDER BY  notice.notificationId"),
  @NamedQuery(name = "AlertNoticeEntity.findByUuid", query = "SELECT notice FROM AlertNoticeEntity notice WHERE notice.uuid = :uuid"),
  @NamedQuery(name = "AlertNoticeEntity.findByHistoryIds", query = "SELECT notice FROM AlertNoticeEntity notice WHERE notice.historyId IN :historyIds"),
  // The remove query can be handled by a simpler JPQL query,
  // however, MySQL gtid enforce policy gets violated due to creation and
  // deletion of TEMP table in the same transaction
  @NamedQuery(name = "AlertNoticeEntity.removeByHistoryIds", query = "DELETE FROM AlertNoticeEntity notice WHERE notice.historyId IN :historyIds")
})
public class AlertNoticeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "alert_notice_id_generator")
  @Column(name = "notification_id", nullable = false, updatable = false)
  private Long notificationId;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "notify_state", nullable = false, length = 255)
  private NotificationState notifyState;

  @Basic
  @Column(nullable = false, length = 64, unique = true)
  private String uuid;

  @ManyToOne
  @JoinColumn(name = "history_id", nullable = false)
  private AlertHistoryEntity alertHistory;

  @Column(name = "history_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long historyId;

  @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
  @JoinColumn(name = "target_id", nullable = false)
  private AlertTargetEntity alertTarget;

}
