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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "hosts")
@TableGenerator(name = "host_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
    , pkColumnValue = "host_id_seq"
    , initialValue = 0
)
@NamedQueries({
    @NamedQuery(name = "HostEntity.findByHostName", query = "SELECT host FROM HostEntity host WHERE host.hostName = :hostName"),
})
public class HostEntity {

  @Id
  @Column(name = "host_id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_id_generator")
  private Long hostId;

  @Column(name = "host_name", nullable = false, insertable = true, updatable = true, unique = true)
  @Basic
  private String hostName;

  @Column(name = "ipv4", nullable = true, insertable = true, updatable = true)
  @Basic
  private String ipv4;

  @Column(name = "ipv6", nullable = true, insertable = true, updatable = true)
  @Basic
  private String ipv6;

  @Column(name="public_host_name", nullable = true, insertable = true, updatable = true)
  @Basic
  private String publicHostName;

  @Column(name = "total_mem", nullable = false, insertable = true, updatable = true)
  @Basic
  private Long totalMem = 0L;

  @Column(name = "cpu_count", nullable = false, insertable = true, updatable = true)
  @Basic
  private Integer cpuCount = 0;

  @Column(name = "ph_cpu_count", nullable = false, insertable = true, updatable = true)
  @Basic
  private Integer phCpuCount = 0;

  @Column(name = "cpu_info", insertable = true, updatable = true)
  @Basic
  private String cpuInfo = "";

  @Column(name = "os_arch", insertable = true, updatable = true)
  @Basic
  private String osArch = "";

  @Column(name = "os_info", insertable = true, updatable = true,
      length = 1000)
  @Basic
  private String osInfo = "";

  @Column(name = "os_type", insertable = true, updatable = true)
  @Basic
  private String osType = "";

  @Column(name = "discovery_status", insertable = true, updatable = true,
      length = 2000)
  @Basic
  private String discoveryStatus = "";

  @Column(name = "last_registration_time", nullable = false, insertable = true, updatable = true)
  @Basic
  private Long lastRegistrationTime = 0L;

  @Column(name = "rack_info", nullable = false, insertable = true, updatable = true)
  @Basic
  private String rackInfo = "/default-rack";

  @Column(name = "host_attributes", insertable = true, updatable = true, length = 20000)
  @Basic
  @Lob
  private String hostAttributes = "";

  @OneToMany(mappedBy = "hostEntity")
  private Collection<HostComponentDesiredStateEntity> hostComponentDesiredStateEntities;

  @OneToMany(mappedBy = "hostEntity")
  private Collection<HostComponentStateEntity> hostComponentStateEntities;

  @OneToMany(mappedBy = "hostEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private Collection<HostVersionEntity> hostVersionEntities;

  @ManyToMany
  @JoinTable(name = "ClusterHostMapping",
      joinColumns = {@JoinColumn(name = "host_id", referencedColumnName = "host_id")},
      inverseJoinColumns = {@JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id")}
  )
  private Collection<ClusterEntity> clusterEntities;

  @OneToOne(mappedBy = "hostEntity", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
  private HostStateEntity hostStateEntity;

  @OneToMany(mappedBy = "hostEntity", cascade = CascadeType.REMOVE)
  private Collection<HostRoleCommandEntity> hostRoleCommandEntities;

}
