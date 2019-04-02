package fr.an.test.ambarijpa;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.State;
import fr.an.test.ambarijpa.state.UpgradeState;

@Entity
@Table(name = "hostcomponentstate")
@TableGenerator(
    name = "hostcomponentstate_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "hostcomponentstate_id_seq",
    initialValue = 0)
@NamedQueries({
    @NamedQuery(
        name = "HostComponentStateEntity.findAll",
        query = "SELECT hcs from HostComponentStateEntity hcs"),
    @NamedQuery(
        name = "HostComponentStateEntity.findByHost",
        query = "SELECT hcs from HostComponentStateEntity hcs WHERE hcs.hostEntity.hostName=:hostName"),
    @NamedQuery(
        name = "HostComponentStateEntity.findByService",
        query = "SELECT hcs from HostComponentStateEntity hcs WHERE hcs.serviceName=:serviceName"),
    @NamedQuery(
        name = "HostComponentStateEntity.findByServiceAndComponent",
        query = "SELECT hcs from HostComponentStateEntity hcs WHERE hcs.serviceName=:serviceName AND hcs.componentName=:componentName"),
    @NamedQuery(
        name = "HostComponentStateEntity.findByServiceComponentAndHost",
        query = "SELECT hcs from HostComponentStateEntity hcs WHERE hcs.serviceName=:serviceName AND hcs.componentName=:componentName AND hcs.hostEntity.hostName=:hostName"),
    @NamedQuery(
        name = "HostComponentStateEntity.findByIndex",
        query = "SELECT hcs from HostComponentStateEntity hcs WHERE hcs.clusterId=:clusterId AND hcs.serviceName=:serviceName AND hcs.componentName=:componentName AND hcs.hostId=:hostId"),
    @NamedQuery(
        name = "HostComponentStateEntity.findByServiceAndComponentAndNotVersion",
        query = "SELECT hcs from HostComponentStateEntity hcs WHERE hcs.serviceName=:serviceName AND hcs.componentName=:componentName AND hcs.version != :version")
})

public class HostComponentStateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "hostcomponentstate_id_generator")
  @Column(name = "id", nullable = false, insertable = true, updatable = false)
  private Long id;

  @Column(name = "cluster_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long clusterId;

  @Column(name = "service_name", nullable = false, insertable = false, updatable = false)
  private String serviceName;

  @Column(name = "host_id", nullable = false, insertable = false, updatable = false)
  private Long hostId;

  @Column(name = "component_name", nullable = false, insertable = false, updatable = false)
  private String componentName;

  /**
   * Version reported by host component during last status update.
   */
  @Column(name = "version", nullable = false, insertable = true, updatable = true)
  private String version = State.UNKNOWN.toString();

  @Enumerated(value = EnumType.STRING)
  @Column(name = "current_state", nullable = false, insertable = true, updatable = true)
  private State currentState = State.INIT;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "upgrade_state", nullable = false, insertable = true, updatable = true)
  private UpgradeState upgradeState = UpgradeState.NONE;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false),
      @JoinColumn(name = "service_name", referencedColumnName = "service_name", nullable = false),
      @JoinColumn(name = "component_name", referencedColumnName = "component_name", nullable = false) })
  private ServiceComponentDesiredStateEntity serviceComponentDesiredStateEntity;

  @ManyToOne
  @JoinColumn(name = "host_id", referencedColumnName = "host_id", nullable = false)
  private HostEntity hostEntity;

}
