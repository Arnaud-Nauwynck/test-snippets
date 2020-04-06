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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "topology_logical_task")
@TableGenerator(name = "topology_logical_task_id_generator", table = "ambari_sequences",
  pkColumnName = "sequence_name", valueColumnName = "sequence_value",
  pkColumnValue = "topology_logical_task_id_seq", initialValue = 0)
@NamedQueries({
  @NamedQuery(name = "TopologyLogicalTaskEntity.findHostTaskIdsByPhysicalTaskIds", query = "SELECT DISTINCT logicaltask.hostTaskId from TopologyLogicalTaskEntity logicaltask WHERE logicaltask.physicalTaskId IN :physicalTaskIds"),
  @NamedQuery(name = "TopologyLogicalTaskEntity.removeByPhysicalTaskIds", query = "DELETE FROM TopologyLogicalTaskEntity logicaltask WHERE logicaltask.physicalTaskId IN :taskIds")
})
public class TopologyLogicalTaskEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "topology_logical_task_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "component", length = 255)
  private String componentName;

  @Column(name = "host_task_id", nullable = false, insertable = false, updatable = false)
  private Long hostTaskId;

  @Column(name = "physical_task_id", nullable = false, insertable = false, updatable = false)
  private Long physicalTaskId;

  @ManyToOne
  @JoinColumn(name = "host_task_id", referencedColumnName = "id", nullable = false)
  private TopologyHostTaskEntity topologyHostTaskEntity;

  @OneToOne
  @JoinColumn(name = "physical_task_id", referencedColumnName = "task_id", nullable = false)
  private HostRoleCommandEntity hostRoleCommandEntity;

}
