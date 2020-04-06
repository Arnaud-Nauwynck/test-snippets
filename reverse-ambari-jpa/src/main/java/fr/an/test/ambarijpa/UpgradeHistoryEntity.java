package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * The {@link UpgradeHistoryEntity} represents the version history of components
 * participating in an upgrade or a downgrade.
 */
@Entity
@Table(
    name = "upgrade_history",
    uniqueConstraints = @UniqueConstraint(
        columnNames = { "upgrade_id", "component_name", "service_name" }))
@TableGenerator(
    name = "upgrade_history_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "upgrade_history_id_seq",
    initialValue = 0)
@NamedQueries({
    @NamedQuery(
        name = "UpgradeHistoryEntity.findAll",
        query = "SELECT upgradeHistory FROM UpgradeHistoryEntity upgradeHistory"),
    @NamedQuery(
        name = "UpgradeHistoryEntity.findByUpgradeId",
        query = "SELECT upgradeHistory FROM UpgradeHistoryEntity upgradeHistory WHERE upgradeHistory.upgradeId = :upgradeId")
})
public class UpgradeHistoryEntity {

  @Id
  @Column(name = "id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "upgrade_history_id_generator")
  private Long id;

  @Column(name = "upgrade_id", nullable = false, insertable = false, updatable = false)
  private Long upgradeId;

  @JoinColumn(name = "upgrade_id", nullable = false)
  private UpgradeEntity upgrade;

  @Column(name = "service_name", nullable = false, insertable = true, updatable = true)
  private String serviceName;

  @Column(name = "component_name", nullable = false, insertable = true, updatable = true)
  private String componentName;

  @ManyToOne
  @JoinColumn(name = "from_repo_version_id", unique = false, nullable = false, insertable = true, updatable = true)
  private RepositoryVersionEntity fromRepositoryVersion = null;

  @ManyToOne
  @JoinColumn(name = "target_repo_version_id", unique = false, nullable = false, insertable = true, updatable = true)
  private RepositoryVersionEntity targetRepositoryVersion = null;

}
