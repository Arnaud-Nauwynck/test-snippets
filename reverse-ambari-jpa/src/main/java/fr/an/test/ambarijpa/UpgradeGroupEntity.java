package fr.an.test.ambarijpa;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Models a single upgrade group as part of an entire {@link UpgradeEntity}.
 * <p/>
 * Since {@link UpgradeGroupEntity} instances are rarely created, yet created in
 * bulk, we have an abnormally high {@code allocationSize}} for the
 * {@link TableGenerator}. This helps prevent locks caused by frequenty queries
 * to the sequence ID table.
 */
@Table(name = "upgrade_group")
@Entity
@TableGenerator(name = "upgrade_group_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "upgrade_group_id_seq",
    initialValue = 0,
    allocationSize = 200)
public class UpgradeGroupEntity {

  @Id
  @Column(name = "upgrade_group_id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "upgrade_group_id_generator")
  private Long upgradeGroupId;

  @Column(name = "upgrade_id", nullable = false, insertable = false, updatable = false)
  private Long upgradeId;

  @Basic
  @Column(name = "group_name", length=255, nullable = false)
  private String groupName;

  @Basic
  @Column(name = "group_title", length=1024, nullable = false)
  private String groupTitle;


  @ManyToOne
  @JoinColumn(name = "upgrade_id", referencedColumnName = "upgrade_id", nullable = false)
  private UpgradeEntity upgradeEntity;

  @OneToMany(mappedBy ="upgradeGroupEntity", cascade = { CascadeType.ALL })
  private List<UpgradeItemEntity> upgradeItems;


}
