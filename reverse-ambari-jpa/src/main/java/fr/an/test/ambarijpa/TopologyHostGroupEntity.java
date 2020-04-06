package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "topology_hostgroup")
@NamedQueries({
  @NamedQuery(name = "TopologyHostGroupEntity.findByRequestIdAndName",
    query = "SELECT req FROM TopologyHostGroupEntity req WHERE req.topologyRequestEntity.id = :requestId AND req.name = :name")
})
@TableGenerator(name = "topology_host_group_id_generator", table = "ambari_sequences",
  pkColumnName = "sequence_name", valueColumnName = "sequence_value",
  pkColumnValue = "topology_host_group_id_seq", initialValue = 0)
public class TopologyHostGroupEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "topology_host_group_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, updatable = false)
  private String name;

  @Column(name = "group_properties")
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String groupProperties;

  @Column(name = "group_attributes")
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String groupAttributes;

  @ManyToOne
  @JoinColumn(name = "request_id", referencedColumnName = "id", nullable = false)
  private TopologyRequestEntity topologyRequestEntity;

  @OneToMany(mappedBy = "topologyHostGroupEntity", cascade = CascadeType.ALL)
  private Collection<TopologyHostInfoEntity> topologyHostInfoEntities;

  @OneToMany(mappedBy = "topologyHostGroupEntity", cascade = CascadeType.ALL)
  private Collection<TopologyHostRequestEntity> topologyHostRequestEntities;

}
