package fr.an.test.ambarijpa;

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
import javax.persistence.UniqueConstraint;

import fr.an.test.ambarijpa.state.RepositoryVersionState;

@Entity
@Table(
    name = "host_version",
    uniqueConstraints = @UniqueConstraint(
        name = "UQ_host_repo",
        columnNames = { "host_id", "repo_version_id" }))
@TableGenerator(
    name = "host_version_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "host_version_id_seq",
    initialValue = 0)
@NamedQueries({
    @NamedQuery(name = "hostVersionByClusterAndStackAndVersion", query =
        "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host JOIN host.clusterEntities clusters " +
            "WHERE clusters.clusterName=:clusterName AND hostVersion.repositoryVersion.stack.stackName=:stackName AND hostVersion.repositoryVersion.stack.stackVersion=:stackVersion AND hostVersion.repositoryVersion.version=:version"),

    @NamedQuery(name = "hostVersionByClusterAndHostname", query =
        "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host JOIN host.clusterEntities clusters " +
            "WHERE clusters.clusterName=:clusterName AND hostVersion.hostEntity.hostName=:hostName"),

    @NamedQuery(name = "hostVersionByHostname", query =
        "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host " +
            "WHERE hostVersion.hostEntity.hostName=:hostName"),

    @NamedQuery(
        name = "findByClusterAndState",
        query = "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host JOIN host.clusterEntities clusters "
            + "WHERE clusters.clusterName=:clusterName AND hostVersion.state=:state"),

    @NamedQuery(
        name = "findByCluster",
        query = "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host JOIN host.clusterEntities clusters "
            + "WHERE clusters.clusterName=:clusterName"),

    @NamedQuery(name = "hostVersionByClusterHostnameAndState", query =
        "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host JOIN host.clusterEntities clusters " +
            "WHERE clusters.clusterName=:clusterName AND hostVersion.hostEntity.hostName=:hostName AND hostVersion.state=:state"),

    @NamedQuery(name = "hostVersionByClusterStackVersionAndHostname", query =
        "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host JOIN host.clusterEntities clusters " +
            "WHERE clusters.clusterName=:clusterName AND hostVersion.repositoryVersion.stack.stackName=:stackName AND hostVersion.repositoryVersion.stack.stackVersion=:stackVersion AND hostVersion.repositoryVersion.version=:version AND " +
            "hostVersion.hostEntity.hostName=:hostName"),

    @NamedQuery(
        name = "findHostVersionByClusterAndRepository",
        query = "SELECT hostVersion FROM HostVersionEntity hostVersion JOIN hostVersion.hostEntity host JOIN host.clusterEntities clusters "
            + "WHERE clusters.clusterId = :clusterId AND hostVersion.repositoryVersion = :repositoryVersion"),

    @NamedQuery(
        name = "hostVersionByRepositoryAndStates",
        query = "SELECT hostVersion FROM HostVersionEntity hostVersion WHERE hostVersion.repositoryVersion = :repositoryVersion AND hostVersion.state IN :states"),

    @NamedQuery(
        name = "findByHostAndRepository",
        query = "SELECT hostVersion FROM HostVersionEntity hostVersion WHERE hostVersion.hostEntity = :host AND hostVersion.repositoryVersion = :repositoryVersion")

})
public class HostVersionEntity {

  @Id
  @Column(name = "id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_version_id_generator")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "repo_version_id", referencedColumnName = "repo_version_id", nullable = false)
  private RepositoryVersionEntity repositoryVersion;

  @Column(name = "host_id", nullable=false, insertable = false, updatable = false)
  private Long hostId;

  @ManyToOne
  @JoinColumn(name = "host_id", referencedColumnName = "host_id", nullable = false)
  private HostEntity hostEntity;

  @Column(name = "state", nullable = false, insertable = true, updatable = true)
  @Enumerated(value = EnumType.STRING)
  private RepositoryVersionState state;

}
