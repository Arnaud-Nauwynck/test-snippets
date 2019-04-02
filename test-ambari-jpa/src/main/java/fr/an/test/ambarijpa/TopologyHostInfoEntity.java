package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "topology_host_info")
@TableGenerator(name = "topology_host_info_id_generator", table = "ambari_sequences",
  pkColumnName = "sequence_name", valueColumnName = "sequence_value",
  pkColumnValue = "topology_host_info_id_seq", initialValue = 0)
public class TopologyHostInfoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "topology_host_info_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "fqdn", length = 255)
  private String fqdn;

  @OneToOne
  @JoinColumn(name="host_id")
  private HostEntity hostEntity;

  @Column(name = "host_count", length = 10)
  private Integer hostCount;

  @Column(name = "predicate", length = 2048)
  private String predicate;

  @ManyToOne
  @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
  private TopologyHostGroupEntity topologyHostGroupEntity;

  @Column(name = "rack_info", length = 255)
  private String rackInfo;

}
