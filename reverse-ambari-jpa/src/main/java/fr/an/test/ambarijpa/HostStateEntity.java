package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.state.HostState;

@Table(name = "hoststate")
@Entity
@NamedQueries({
    @NamedQuery(name = "hostStateByHostId", query =
        "SELECT hostState FROM HostStateEntity hostState " +
            "WHERE hostState.hostId=:hostId"),
})
public class HostStateEntity {
  
  @Column(name = "host_id", nullable = false, insertable = false, updatable = false)
  @Id
  private Long hostId;

  @Column(name = "available_mem", nullable = false, insertable = true, updatable = true)
  @Basic
  private Long availableMem = 0L;

  @javax.persistence.Column(name = "time_in_state", nullable = false, insertable = true, updatable = true)
  @Basic
  private Long timeInState = 0L;

  @Column(name = "health_status", insertable = true, updatable = true)
  @Basic
  private String healthStatus;

  @Column(name = "agent_version", insertable = true, updatable = true)
  @Basic
  private String agentVersion = "";

  @Column(name = "current_state", nullable = false, insertable = true, updatable = true)
  @Enumerated(value = EnumType.STRING)
  private HostState currentState = HostState.INIT;
  
  @Column(name="maintenance_state", nullable = true, insertable = true, updatable = true)
  private String maintenanceState = null;
  

  @OneToOne
  @JoinColumn(name = "host_id", referencedColumnName = "host_id", nullable = false)
  private HostEntity hostEntity;


}
