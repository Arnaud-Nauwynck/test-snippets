package fr.an.test.ambarijpa;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.AlertState;

/**
 * The {@link AlertTargetEntity} class represents audience that will receive
 * dispatches when an alert is triggered.
 */
@Entity
@Table(name = "alert_target")
@TableGenerator(
    name = "alert_target_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "alert_target_id_seq",
    initialValue = 0)
@NamedQueries({
    @NamedQuery(
        name = "AlertTargetEntity.findAll",
        query = "SELECT alertTarget FROM AlertTargetEntity alertTarget"),
    @NamedQuery(
        name = "AlertTargetEntity.findAllGlobal",
        query = "SELECT alertTarget FROM AlertTargetEntity alertTarget WHERE alertTarget.isGlobal = 1"),
    @NamedQuery(
        name = "AlertTargetEntity.findByName",
        query = "SELECT alertTarget FROM AlertTargetEntity alertTarget WHERE alertTarget.targetName = :targetName"),
    @NamedQuery(
        name = "AlertTargetEntity.findByIds",
        query = "SELECT alertTarget FROM AlertTargetEntity alertTarget WHERE alertTarget.targetId IN :targetIds") })
public class AlertTargetEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "alert_target_id_generator")
  @Column(name = "target_id", nullable = false, updatable = false)
  private Long targetId;

  @Column(length = 1024)
  private String description;

  @Column(name = "notification_type", nullable = false, length = 64)
  private String notificationType;

  @Lob
  @Basic
  @Column(length = 32672)
  private String properties;

  @Column(name = "target_name", unique = true, nullable = false, length = 255)
  private String targetName;

  @Column(name = "is_global", nullable = false, length = 1)
  private Short isGlobal = Short.valueOf((short) 0);

  @Column(name = "is_enabled", nullable = false, length = 1)
  private Short isEnabled = Short.valueOf((short) 1);


  @ManyToMany(
      fetch = FetchType.EAGER,
      mappedBy = "alertTargets",
      cascade = { CascadeType.MERGE, CascadeType.REFRESH })
  private Set<AlertGroupEntity> alertGroups;

  /**
   * Gets the alert states that this target will be notified for. If this is
   * either empty or {@code null}, then it is implied that all alert states are
   * of interest to the target. A target without an alert states does not make
   * sense which is why the absence of states implies all states.
   */
  @Enumerated(value = EnumType.STRING)
  @ElementCollection(targetClass = AlertState.class)
  @CollectionTable(name = "alert_target_states", joinColumns = @JoinColumn(name = "target_id"))
  @Column(name = "alert_state")
  private Set<AlertState> alertStates = EnumSet.allOf(AlertState.class);

  /**
   * Bi-directional one-to-many association to {@link AlertNoticeEntity}.
   */
  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "alertTarget")
  private List<AlertNoticeEntity> alertNotices;

}
