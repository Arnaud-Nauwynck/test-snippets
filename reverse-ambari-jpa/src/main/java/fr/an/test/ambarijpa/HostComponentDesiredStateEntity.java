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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import fr.an.test.ambarijpa.state.BlueprintProvisioningState;
import fr.an.test.ambarijpa.state.HostComponentAdminState;
import fr.an.test.ambarijpa.state.MaintenanceState;
import fr.an.test.ambarijpa.state.State;

@Entity
@Table(
  name = "hostcomponentdesiredstate",
  uniqueConstraints = @UniqueConstraint(
    name = "UQ_hcdesiredstate_name",
    columnNames = { "component_name", "service_name", "host_id", "cluster_id" }) )
@TableGenerator(
  name = "hostcomponentdesiredstate_id_generator",
  table = "ambari_sequences",
  pkColumnName = "sequence_name",
  valueColumnName = "sequence_value",
  pkColumnValue = "hostcomponentdesiredstate_id_seq",
  initialValue = 0)
@NamedQueries({
    @NamedQuery(name = "HostComponentDesiredStateEntity.findAll", query = "SELECT hcds from HostComponentDesiredStateEntity hcds"),

    @NamedQuery(name = "HostComponentDesiredStateEntity.findByServiceAndComponent", query =
        "SELECT hcds from HostComponentDesiredStateEntity hcds WHERE hcds.serviceName=:serviceName AND hcds.componentName=:componentName"),

    @NamedQuery(name = "HostComponentDesiredStateEntity.findByServiceComponentAndHost", query =
        "SELECT hcds from HostComponentDesiredStateEntity hcds WHERE hcds.serviceName=:serviceName AND hcds.componentName=:componentName AND hcds.hostEntity.hostName=:hostName"),

  @NamedQuery(name = "HostComponentDesiredStateEntity.findByIndexAndHost", query =
    "SELECT hcds from HostComponentDesiredStateEntity hcds WHERE hcds.clusterId=:clusterId AND hcds.serviceName=:serviceName AND hcds.componentName=:componentName AND hcds.hostId=:hostId"),

  @NamedQuery(name = "HostComponentDesiredStateEntity.findByIndex", query =
    "SELECT hcds from HostComponentDesiredStateEntity hcds WHERE hcds.clusterId=:clusterId AND hcds.serviceName=:serviceName AND hcds.componentName=:componentName"),

  @NamedQuery(name = "HostComponentDesiredStateEntity.findByHostsAndCluster", query =
    "SELECT hcds from HostComponentDesiredStateEntity hcds WHERE hcds.hostId IN :hostIds AND hcds.clusterId=:clusterId"),
})
public class HostComponentDesiredStateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "hostcomponentdesiredstate_id_generator")
  @Column(name = "id", nullable = false, insertable = true, updatable = false)
  private Long id;


  @Column(name = "cluster_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long clusterId;

  @Column(name = "service_name", nullable = false, insertable = false, updatable = false)
  private String serviceName;

  @Column(name = "host_id", nullable = false, insertable = false, updatable = false)
  private Long hostId;

  @Column(name = "component_name", insertable = false, updatable = false)
  private String componentName = "";

  @Basic
  @Column(name = "desired_state", nullable = false, insertable = true, updatable = true)
  @Enumerated(value = EnumType.STRING)
  private State desiredState = State.INIT;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "admin_state", nullable = true, insertable = true, updatable = true)
  private HostComponentAdminState adminState;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false),
      @JoinColumn(name = "service_name", referencedColumnName = "service_name", nullable = false),
      @JoinColumn(name = "component_name", referencedColumnName = "component_name", nullable = false)})
  private ServiceComponentDesiredStateEntity serviceComponentDesiredStateEntity;

  @ManyToOne
  @JoinColumn(name = "host_id", referencedColumnName = "host_id", nullable = false)
  private HostEntity hostEntity;

  @Enumerated(value = EnumType.STRING)
  @Column(name="maintenance_state", nullable = false, insertable = true, updatable = true)
  private MaintenanceState maintenanceState = MaintenanceState.OFF;

  @Basic
  @Column(name = "restart_required", insertable = true, updatable = true, nullable = false)
  private Integer restartRequired = 0;

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "blueprint_provisioning_state", insertable = true, updatable = true)
  private BlueprintProvisioningState blueprintProvisioningState = BlueprintProvisioningState.NONE;

}
