package fr.an.test.ambarijpa;

import java.util.List;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.RepositoryType;

/**
 * Models the data representation of an upgrade
 */
@Entity
@Table(name = "upgrade")
@TableGenerator(
    name = "upgrade_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "upgrade_id_seq",
    initialValue = 0)
@NamedQueries({
    @NamedQuery(name = "UpgradeEntity.findAll", query = "SELECT u FROM UpgradeEntity u"),
    @NamedQuery(
        name = "UpgradeEntity.findAllForCluster",
        query = "SELECT u FROM UpgradeEntity u WHERE u.clusterId = :clusterId"),
    @NamedQuery(
        name = "UpgradeEntity.findUpgrade",
        query = "SELECT u FROM UpgradeEntity u WHERE u.upgradeId = :upgradeId"),
    @NamedQuery(
        name = "UpgradeEntity.findUpgradeByRequestId",
        query = "SELECT u FROM UpgradeEntity u WHERE u.requestId = :requestId"),
    @NamedQuery(
        name = "UpgradeEntity.findLatestForClusterInDirection",
        query = "SELECT u FROM UpgradeEntity u JOIN RequestEntity r ON u.requestId = r.requestId WHERE u.clusterId = :clusterId AND u.direction = :direction ORDER BY r.startTime DESC, u.upgradeId DESC"),
    @NamedQuery(
        name = "UpgradeEntity.findLatestForCluster",
        query = "SELECT u FROM UpgradeEntity u JOIN RequestEntity r ON u.requestId = r.requestId WHERE u.clusterId = :clusterId ORDER BY r.startTime DESC"),
    @NamedQuery(
        name = "UpgradeEntity.findAllRequestIds",
        query = "SELECT upgrade.requestId FROM UpgradeEntity upgrade"),
    @NamedQuery(
        name = "UpgradeEntity.findRevertable",
        query = "SELECT upgrade FROM UpgradeEntity upgrade WHERE upgrade.revertAllowed = 1 AND upgrade.clusterId = :clusterId ORDER BY upgrade.upgradeId DESC",
        hints = {
            @QueryHint(name = "eclipselink.query-results-cache", value = "true"),
            @QueryHint(name = "eclipselink.query-results-cache.ignore-null", value = "false"),
            @QueryHint(name = "eclipselink.query-results-cache.size", value = "1")
          }),
    @NamedQuery(
        name = "UpgradeEntity.findRevertableUsingJPQL",
        query = "SELECT upgrade FROM UpgradeEntity upgrade WHERE upgrade.repoVersionId IN (SELECT upgrade.repoVersionId FROM UpgradeEntity upgrade WHERE upgrade.clusterId = :clusterId AND upgrade.orchestration IN :revertableTypes GROUP BY upgrade.repoVersionId HAVING MOD(COUNT(upgrade.repoVersionId), 2) != 0) ORDER BY upgrade.upgradeId DESC",
        hints = {
            @QueryHint(name = "eclipselink.query-results-cache", value = "true"),
            @QueryHint(name = "eclipselink.query-results-cache.ignore-null", value = "false"),
            @QueryHint(name = "eclipselink.query-results-cache.size", value = "1")
          })
        })
public class UpgradeEntity {

  @Id
  @Column(name = "upgrade_id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "upgrade_id_generator")
  private Long upgradeId;

  @Column(name = "cluster_id", nullable = false, insertable = true, updatable = false)
  private Long clusterId;

  @Column(name = "request_id", nullable = false, insertable = false, updatable = false)
  private Long requestId;

  /**
   * The request entity associated with this upgrade. This relationship allows
   * JPA to correctly order non-flushed commits during the transaction which
   * creates the upgrade. Without it, JPA would not know the correct order and
   * may try to create the upgrade before the request.
   */
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "request_id", nullable = false, insertable = true, updatable = false)
  private RequestEntity requestEntity = null;

  public enum Direction {
	  UPGRADE,
	  DOWNGRADE;
  }
  
  @Column(name="direction", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private Direction direction = Direction.UPGRADE;

  @Column(name="upgrade_package", nullable = false)
  private String upgradePackage;

  @Column(name="upgrade_package_stack", nullable = false)
  private String upgradePackStack;

  enum UpgradeType {
	  ROLLING,
	  NON_ROLLING,
	  HOST_ORDERED;
  }
  
  @Column(name="upgrade_type", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private UpgradeType upgradeType;

  @Column(name = "repo_version_id", insertable = false, updatable = false)
  private Long repoVersionId;

  @JoinColumn(name = "repo_version_id", referencedColumnName = "repo_version_id", nullable = false)
  private RepositoryVersionEntity repositoryVersion;

  @Column(name = "skip_failures", nullable = false)
  private Integer skipFailures = 0;

  @Column(name = "skip_sc_failures", nullable = false)
  private Integer skipServiceCheckFailures = 0;

  @Column(name="downgrade_allowed", nullable = false)
  private Short downgradeAllowed = 1;

  /**
   * Whether this upgrade is a candidate to be reverted. The current restriction
   * on this behavior is that only the most recent
   * {@link RepositoryType#PATCH}/{@link RepositoryType#MAINT} for a given
   * cluster can be reverted at a time.
   * <p/>
   * All upgrades are created with this value defaulted to {@code false}. Upon
   * successful finalization of the upgrade, if the upgrade was the correct type
   * and direction, then it becomes a candidate for reversion and this value is
   * set to {@code true}. If an upgrade is reverted after being finalized, then
   * this value to should set to {@code false} explicitely.
   * <p/>
   * There can exist <i>n</i> number of upgrades with this value set to
   * {@code true}. The idea is that only the most recent upgrade with this value
   * set to {@code true} will be able to be reverted.
   */
  @Column(name = "revert_allowed", nullable = false)
  private Short revertAllowed = 0;

  @Column(name="orchestration", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private RepositoryType orchestration = RepositoryType.STANDARD;

  /**
   * {@code true} if the upgrade has been marked as suspended.
   */
  @Column(name = "suspended", nullable = false, length = 1)
  private Short suspended = 0;

  @OneToMany(mappedBy = "upgradeEntity", cascade = { CascadeType.ALL })
  private List<UpgradeGroupEntity> upgradeGroupEntities;

  /**
   * Uni-directional relationship between an upgrade an all of the components in
   * that upgrade.
   */
  @OneToMany(orphanRemoval=true, cascade = { CascadeType.ALL })
  @JoinColumn(name = "upgrade_id")
  private List<UpgradeHistoryEntity> upgradeHistory;

}
