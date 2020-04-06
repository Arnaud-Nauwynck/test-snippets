package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import fr.an.test.ambarijpa.state.RepositoryVersionState;
import fr.an.test.ambarijpa.state.State;

@Entity
@Table(
    name = "servicecomponentdesiredstate",
    uniqueConstraints = @UniqueConstraint(
        name = "unq_scdesiredstate_name",
        columnNames = { "component_name", "service_name", "cluster_id" }) )
@TableGenerator(
    name = "servicecomponentdesiredstate_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "servicecomponentdesiredstate_id_seq",
    initialValue = 0)
@NamedQueries({
 @NamedQuery(
    name = "ServiceComponentDesiredStateEntity.findByName",
    query = "SELECT scds FROM ServiceComponentDesiredStateEntity scds WHERE scds.clusterId = :clusterId AND scds.serviceName = :serviceName AND scds.componentName = :componentName") })
public class ServiceComponentDesiredStateEntity {

  @Id
  @Column(name = "id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(
      strategy = GenerationType.TABLE,
      generator = "servicecomponentdesiredstate_id_generator")
  private Long id;

  @Column(name = "cluster_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long clusterId;

  @Column(name = "service_name", nullable = false, insertable = false, updatable = false)
  private String serviceName;

  @Column(name = "component_name", nullable = false, insertable = true, updatable = true)
  private String componentName;

  @Column(name = "desired_state", nullable = false, insertable = true, updatable = true)
  @Enumerated(EnumType.STRING)
  private State desiredState = State.INIT;

  @Column(name = "recovery_enabled", nullable = false, insertable = true, updatable = true)
  private Integer recoveryEnabled = 0;

  @Column(name = "repo_state", nullable = false, insertable = true, updatable = true)
  @Enumerated(EnumType.STRING)
  private RepositoryVersionState repoState = RepositoryVersionState.NOT_REQUIRED;

  /**
   * Unidirectional one-to-one association to {@link RepositoryVersionEntity}
   */
  @OneToOne
  @JoinColumn(
      name = "desired_repo_version_id",
      unique = false,
      nullable = false,
      insertable = true,
      updatable = true)
  private RepositoryVersionEntity desiredRepositoryVersion;

  @ManyToOne
  @JoinColumns({@javax.persistence.JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false), @JoinColumn(name = "service_name", referencedColumnName = "service_name", nullable = false)})
  private ClusterServiceEntity clusterServiceEntity;

  @OneToMany(mappedBy = "serviceComponentDesiredStateEntity")
  private Collection<HostComponentStateEntity> hostComponentStateEntities;

  @OneToMany(mappedBy = "serviceComponentDesiredStateEntity")
  private Collection<HostComponentDesiredStateEntity> hostComponentDesiredStateEntities;

  @OneToMany(mappedBy = "m_serviceComponentDesiredStateEntity", cascade = { CascadeType.ALL })
  private Collection<ServiceComponentVersionEntity> serviceComponentVersions;


}
