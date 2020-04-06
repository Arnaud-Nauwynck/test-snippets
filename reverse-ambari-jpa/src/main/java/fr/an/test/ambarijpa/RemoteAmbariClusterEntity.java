package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Remote Ambari Managed Cluster
 */
@Table(name = "remoteambaricluster")
@TableGenerator(name = "remote_cluster_id_generator",
  table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
  , pkColumnValue = "remote_cluster_id_seq"
  , initialValue = 1
)

@NamedQueries({
  @NamedQuery(name = "allRemoteAmbariClusters",
    query = "SELECT remoteAmbariCluster FROM RemoteAmbariClusterEntity remoteambaricluster"),
  @NamedQuery(name = "remoteAmbariClusterByName", query =
    "SELECT remoteAmbariCluster " +
      "FROM RemoteAmbariClusterEntity remoteAmbariCluster " +
      "WHERE remoteAmbariCluster.name=:clusterName"),
  @NamedQuery(name = "remoteAmbariClusterById", query =
    "SELECT remoteAmbariCluster " +
      "FROM RemoteAmbariClusterEntity remoteAmbariCluster " +
      "WHERE remoteAmbariCluster.id=:clusterId")})
@Entity
public class RemoteAmbariClusterEntity {

  @Id
  @Column(name = "cluster_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "remote_cluster_id_generator")
  private Long id;

  @Column(name = "name", nullable = false, insertable = true, updatable = true)
  private String name;

  @Column(name = "url", nullable = false, insertable = true, updatable = true)
  private String url;

  @Column(name = "username", nullable = false, insertable = true, updatable = true)
  private String username;

  @Column(name = "password", nullable = false, insertable = true, updatable = true)
  private String password;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "cluster")
  private Collection<RemoteAmbariClusterServiceEntity> services;

}
