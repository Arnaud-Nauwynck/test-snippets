package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.an.test.ambarijpa.state.HostRoleStatus;

@Entity
@Table(name = "topology_host_request")
@NamedQueries({
  @NamedQuery(name = "TopologyHostRequestEntity.removeByIds", query = "DELETE FROM TopologyHostRequestEntity topologyHostRequest WHERE topologyHostRequest.id IN :hostRequestIds")
})
public class TopologyHostRequestEntity {
  @Id
//  @GeneratedValue(strategy = GenerationType.TABLE, generator = "topology_host_request_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "stage_id", length = 10, nullable = false)
  private Long stageId;

  @Column(name = "host_name", length = 255)
  private String hostName;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private HostRoleStatus status;

  @Column(name = "status_message")
  private String statusMessage;

  @ManyToOne
  @JoinColumn(name = "logical_request_id", referencedColumnName = "id", nullable = false)
  private TopologyLogicalRequestEntity topologyLogicalRequestEntity;

  @ManyToOne
  @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
  private TopologyHostGroupEntity topologyHostGroupEntity;

  @OneToMany(mappedBy = "topologyHostRequestEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<TopologyHostTaskEntity> topologyHostTaskEntities;

}
