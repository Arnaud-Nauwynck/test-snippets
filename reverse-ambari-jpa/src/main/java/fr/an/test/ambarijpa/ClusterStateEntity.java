package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@javax.persistence.Table(name = "clusterstate")
@Entity
public class ClusterStateEntity {

  @Id
  @Column(name = "cluster_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long clusterId;

  @Basic
  @Column(name = "current_cluster_state", insertable = true, updatable = true)
  private String currentClusterState = "";

  @OneToOne
  @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false)
  private ClusterEntity clusterEntity;

  /**
   * Unidirectional one-to-one association to {@link StackEntity}
   */
  @OneToOne
  @JoinColumn(name = "current_stack_id", unique = false, nullable = false, insertable = true, updatable = true)
  private StackEntity currentStack;

}
