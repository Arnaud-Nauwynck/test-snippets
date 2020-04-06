package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.ProvisionAction;

@Entity
@Table(name = "topology_request")
@TableGenerator(name = "topology_request_id_generator", table = "ambari_sequences",
                pkColumnName = "sequence_name", valueColumnName = "sequence_value",
                pkColumnValue = "topology_request_id_seq", initialValue = 0)
@NamedQueries({
  @NamedQuery(name = "TopologyRequestEntity.findByClusterId", query = "SELECT req FROM TopologyRequestEntity req WHERE req.clusterId = :clusterId"),
  @NamedQuery(name = "TopologyRequestEntity.findProvisionRequests", query = "SELECT req FROM TopologyRequestEntity req WHERE req.action = 'PROVISION'"),
})
public class TopologyRequestEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "topology_request_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "action", length = 255, nullable = false)
  private String action;

  @Column(name = "cluster_id", nullable = true)
  private Long clusterId;

  @Column(name = "bp_name", length = 100, nullable = false)
  private String blueprintName;

  @Column(name = "cluster_properties")
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String clusterProperties;

  @Column(name = "cluster_attributes")
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String clusterAttributes;

  @Column(name = "description", length = 1024, nullable = false)
  private String description;

  @OneToMany(mappedBy = "topologyRequestEntity", cascade = CascadeType.ALL)
  private Collection<TopologyHostGroupEntity> topologyHostGroupEntities;

  @OneToOne(mappedBy = "topologyRequestEntity", cascade = CascadeType.ALL)
  private TopologyLogicalRequestEntity topologyLogicalRequestEntity;

  @Column(name = "provision_action", length = 255, nullable = true)
  @Enumerated(EnumType.STRING)
  private ProvisionAction provisionAction;

}
