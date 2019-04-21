package fr.an.test.ambarijpa;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import fr.an.test.ambarijpa.ClusterServiceEntity.ClusterServiceEntityPK;

@javax.persistence.IdClass(ClusterServiceEntityPK.class)
@javax.persistence.Table(name = "clusterservices")
@NamedQueries({
        @NamedQuery(name = "clusterServiceByClusterAndServiceNames", query =
                "SELECT clusterService " +
                        "FROM ClusterServiceEntity clusterService " +
                        "JOIN clusterService.clusterEntity cluster " +
                        "WHERE clusterService.serviceName=:serviceName AND cluster.clusterName=:clusterName")
})
@Entity
public class ClusterServiceEntity {

  @Id
  @Column(name = "cluster_id", nullable = false, insertable = false, updatable = false, length = 10)
  private Long clusterId;

  @Id
  @Column(name = "service_name", nullable = false, insertable = true, updatable = true)
  private String serviceName;

  @SuppressWarnings("serial")
  public static class ClusterServiceEntityPK implements Serializable {

  	@Id
  	@Column(name = "cluster_id", nullable = false, insertable = true, updatable = true, length = 10)
  	private Long clusterId;

  	@Id
  	@Column(name = "service_name", nullable = false, insertable = true, updatable = true)
  	private String serviceName;

  }

  @Basic
  @Column(name = "service_enabled", nullable = false, insertable = true, updatable = true, length = 10)
  private Integer serviceEnabled = 0;

  @ManyToOne
  @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false)
  private ClusterEntity clusterEntity;

  @OneToOne(mappedBy = "clusterServiceEntity", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  private ServiceDesiredStateEntity serviceDesiredStateEntity;

  @OneToMany(mappedBy = "clusterServiceEntity")
  private Collection<ServiceComponentDesiredStateEntity> serviceComponentDesiredStateEntities;

  public Long getClusterId() {
    return clusterId;
  }

  public void setClusterId(Long clusterId) {
    this.clusterId = clusterId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public int getServiceEnabled() {
    return serviceEnabled;
  }

  public void setServiceEnabled(int serviceEnabled) {
    this.serviceEnabled = serviceEnabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ClusterServiceEntity that = (ClusterServiceEntity) o;

    if (clusterId != null ? !clusterId.equals(that.clusterId) : that.clusterId != null) return false;
    if (serviceEnabled != null ? !serviceEnabled.equals(that.serviceEnabled) : that.serviceEnabled != null)
      return false;
    if (serviceName != null ? !serviceName.equals(that.serviceName) : that.serviceName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = clusterId !=null ? clusterId.intValue() : 0;
    result = 31 * result + (serviceName != null ? serviceName.hashCode() : 0);
    result = 31 * result + serviceEnabled;
    return result;
  }

  public ClusterEntity getClusterEntity() {
    return clusterEntity;
  }

  public void setClusterEntity(ClusterEntity clusterEntity) {
    this.clusterEntity = clusterEntity;
  }

  public ServiceDesiredStateEntity getServiceDesiredStateEntity() {
    return serviceDesiredStateEntity;
  }

  public void setServiceDesiredStateEntity(ServiceDesiredStateEntity serviceDesiredStateEntity) {
    this.serviceDesiredStateEntity = serviceDesiredStateEntity;
  }

  public Collection<ServiceComponentDesiredStateEntity> getServiceComponentDesiredStateEntities() {
    return serviceComponentDesiredStateEntities;
  }

  public void setServiceComponentDesiredStateEntities(Collection<ServiceComponentDesiredStateEntity> serviceComponentDesiredStateEntities) {
    this.serviceComponentDesiredStateEntities = serviceComponentDesiredStateEntities;
  }

}
