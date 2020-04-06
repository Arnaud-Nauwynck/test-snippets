package fr.an.test.ambarijpa;

import javax.persistence.Basic;
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

import fr.an.test.ambarijpa.state.UpgradeState;

/**
 * Models a single upgrade item which is directly associated with {@link Stage}.
 * <p/>
 * Since {@link UpgradeItemEntity} instances are rarely created, yet created in
 * bulk, we have an abnormally high {@code allocationSize}} for the
 * {@link TableGenerator}. This helps prevent locks caused by frequenty queries
 * to the sequence ID table.
 */
@Table(name = "upgrade_item")
@Entity
@TableGenerator(name = "upgrade_item_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "upgrade_item_id_seq",
    initialValue = 0,
    allocationSize = 1000)
@NamedQueries({
  @NamedQuery(name = "UpgradeItemEntity.findAllStageIds", query = "SELECT upgradeItem.stageId FROM UpgradeItemEntity upgradeItem")
})
public class UpgradeItemEntity {

  @Id
  @Column(name = "upgrade_item_id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "upgrade_item_id_generator")
  private Long upgradeItemId;

  @Column(name = "upgrade_group_id", nullable = false, insertable = false, updatable = false)
  private Long upgradeGroupId;

  @Enumerated(value=EnumType.STRING)
  @Column(name = "state", length=255, nullable = false)
  private UpgradeState state = UpgradeState.NONE;

  @Basic
  @Column(name = "hosts")
  private String hosts = null;

  @Basic
  @Column(name = "tasks", length=4096)
  private String tasks = null;

  @Basic
  @Column(name = "item_text")
  private String itemText = null;

  @Basic
  @Column(name = "stage_id", nullable = false)
  private Long stageId = Long.valueOf(0L);

  @ManyToOne
  @JoinColumn(name = "upgrade_group_id", referencedColumnName = "upgrade_group_id", nullable = false)
  private UpgradeGroupEntity upgradeGroupEntity;

}
