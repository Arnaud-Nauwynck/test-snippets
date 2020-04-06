package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.state.HostRoleStatus;
import fr.an.test.ambarijpa.state.RequestType;

@Table(name = "request")
@Entity
@NamedQueries({
  @NamedQuery(name = "RequestEntity.findRequestStageIdsInClusterBeforeDate", query = "SELECT NEW org.apache.ambari.server.orm.dao.RequestDAO.StageEntityPK(request.requestId, stage.stageId) FROM RequestEntity request JOIN StageEntity stage ON request.requestId = stage.requestId WHERE request.clusterId = :clusterId AND request.createTime <= :beforeDate"),
  @NamedQuery(name = "RequestEntity.removeByRequestIds", query = "DELETE FROM RequestEntity request WHERE request.requestId IN :requestIds")
})
public class RequestEntity {

  @Column(name = "request_id")
  @Id
  private Long requestId;

  @Column(name = "cluster_id", updatable = false, nullable = false)
  @Basic
  private Long clusterId;

  @Column(name = "request_schedule_id", updatable = false, insertable = false, nullable = true)
  @Basic
  private Long requestScheduleId;

  @Column(name = "request_context")
  @Basic
  private String requestContext;

  @Column(name = "command_name")
  @Basic
  private String commandName;

  /**
   * On large clusters, this value can be in the 10,000's of kilobytes. During
   * an upgrade, all stages are loaded in memory for every request, which can
   * lead to an OOM. As a result, lazy load this since it's barely ever
   * requested or used.
   */
  @Column(name = "cluster_host_info")
  @Basic(fetch = FetchType.LAZY)
  private byte[] clusterHostInfo;

  @Column(name = "inputs")
  @Lob
  private byte[] inputs = new byte[0];

  @Column(name = "request_type")
  @Enumerated(value = EnumType.STRING)
  private RequestType requestType;

  /**
   * This is the logical status of the request and
   * represents if the intent of the request has been accomplished or not
   *
   *  Status calculated by calculating {@link StageEntity#status} of all belonging stages
   *
   */
  @Column(name = "status", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private HostRoleStatus status = HostRoleStatus.PENDING;

  /**
   * This status informs if any of the underlying tasks
   * have faced any type of failures {@link HostRoleStatus#isFailedState()}
   *
   * Status calculated by only taking into account
   * all belonging {@link HostRoleCommandEntity#status} (or {@link StageEntity#status})
   *
   */
  @Column(name = "display_status", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private HostRoleStatus displayStatus = HostRoleStatus.PENDING;

  @Basic
  @Column(name = "create_time", nullable = false)
  private Long createTime = System.currentTimeMillis();

  @Basic
  @Column(name = "start_time", nullable = false)
  private Long startTime = -1L;

  @Basic
  @Column(name = "end_time", nullable = false)
  private Long endTime = -1L;

  @Basic
  @Column(name = "exclusive_execution", insertable = true, updatable = true, nullable = false)
  private Integer exclusive = 0;

  @Column(name = "user_name")
  private String userName;

  @OneToMany(mappedBy = "request", cascade = CascadeType.REMOVE)
  private Collection<StageEntity> stages;

  @OneToMany(mappedBy = "requestEntity", cascade = CascadeType.ALL)
  private Collection<RequestResourceFilterEntity> resourceFilterEntities;

  @OneToOne(mappedBy = "requestEntity", cascade = {CascadeType.ALL})
  private RequestOperationLevelEntity requestOperationLevel;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name = "request_schedule_id", referencedColumnName = "schedule_id")
  private RequestScheduleEntity requestScheduleEntity;

}
