package fr.an.test.ambarijpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import fr.an.test.ambarijpa.state.RepositoryType;

@Entity
@Table(name = "repo_version", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"display_name"}),
    @UniqueConstraint(columnNames = {"stack_id", "version"})
})
@TableGenerator(name = "repository_version_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "repo_version_id_seq"
    )
@NamedQueries({
    @NamedQuery(
        name = "repositoryVersionByDisplayName",
        query = "SELECT repoversion FROM RepositoryVersionEntity repoversion WHERE repoversion.displayName=:displayname"),
    @NamedQuery(
        name = "repositoryVersionByStack",
        query = "SELECT repoversion FROM RepositoryVersionEntity repoversion WHERE repoversion.stack.stackName=:stackName AND repoversion.stack.stackVersion=:stackVersion"),
    @NamedQuery(
        name = "repositoryVersionByStackAndType",
        query = "SELECT repoversion FROM RepositoryVersionEntity repoversion WHERE repoversion.stack.stackName=:stackName AND repoversion.stack.stackVersion=:stackVersion AND repoversion.type=:type"),
    @NamedQuery(
        name = "repositoryVersionByStackNameAndVersion",
        query = "SELECT repoversion FROM RepositoryVersionEntity repoversion WHERE repoversion.stack.stackName=:stackName AND repoversion.version=:version"),
    @NamedQuery(
        name = "repositoryVersionsFromDefinition",
        query = "SELECT repoversion FROM RepositoryVersionEntity repoversion WHERE repoversion.versionXsd IS NOT NULL"),
    @NamedQuery(
        name = "findRepositoryByVersion",
        query = "SELECT repositoryVersion FROM RepositoryVersionEntity repositoryVersion WHERE repositoryVersion.version = :version ORDER BY repositoryVersion.id DESC"),
    @NamedQuery(
        name = "findByServiceDesiredVersion",
        query = "SELECT repositoryVersion FROM RepositoryVersionEntity repositoryVersion WHERE repositoryVersion IN (SELECT DISTINCT sd1.desiredRepositoryVersion FROM ServiceDesiredStateEntity sd1 WHERE sd1.desiredRepositoryVersion IN ?1)") })
public class RepositoryVersionEntity {

  @Id
  @Column(name = "repo_version_id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "repository_version_id_generator")
  private Long id;

  /**
   * Unidirectional one-to-one association to {@link StackEntity}
   */
  @OneToOne
  @JoinColumn(name = "stack_id", nullable = false)
  private StackEntity stack;

  @Column(name = "version")
  private String version;

  @Column(name = "display_name")
  private String displayName;

  /**
   * one-to-many association to {@link RepoOsEntity}
   */
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "repositoryVersionEntity", orphanRemoval = true)
  private List<RepoOsEntity> repoOsEntities = new ArrayList<>();

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "repositoryVersion")
  private Set<HostVersionEntity> hostVersionEntities;
  
  @Column(name = "repo_type", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private RepositoryType type = RepositoryType.STANDARD;

  @Lob
  @Column(name="version_xml")
  private String versionXml;

  @Column(name="version_url")
  private String versionUrl;

  @Column(name="version_xsd")
  private String versionXsd;

  @Column(name = "hidden", nullable = false)
  private short isHidden = 0;

  /**
   * Repositories can't be trusted until they have been deployed and we've
   * detected their actual version. Most of the time, things match up, but
   * editing a VDF could causes the version to be misrepresented. Once we have
   * received the correct version of the repository (normally after it's been
   * installed), then we can set this flag to {@code true}.
   */
  @Column(name = "resolved", nullable = false)
  private short resolved = 0;

  @Column(name = "legacy", nullable = false)
  private short isLegacy = 0;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private RepositoryVersionEntity parent;

  @OneToMany(mappedBy = "parent")
  private List<RepositoryVersionEntity> children;

}
