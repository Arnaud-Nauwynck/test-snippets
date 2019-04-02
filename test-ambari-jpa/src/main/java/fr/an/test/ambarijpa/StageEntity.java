package fr.an.test.ambarijpa;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.an.test.ambarijpa.StageEntity.StageEntityPK;
import fr.an.test.ambarijpa.state.CommandExecutionType;
import fr.an.test.ambarijpa.state.HostRoleStatus;

@Entity
@Table(name = "stage")
@IdClass(StageEntityPK.class)
@NamedQueries({
    @NamedQuery(
        name = "StageEntity.findFirstStageByStatus",
        query = "SELECT stage.requestId, MIN(stage.stageId) from StageEntity stage, HostRoleCommandEntity hrc WHERE hrc.status IN :statuses AND hrc.stageId = stage.stageId AND hrc.requestId = stage.requestId GROUP by stage.requestId ORDER BY stage.requestId"),
    @NamedQuery(
        name = "StageEntity.findByRequestIdAndCommandStatuses",
        query = "SELECT stage from StageEntity stage WHERE stage.status IN :statuses AND stage.requestId = :requestId ORDER BY stage.stageId"),
    @NamedQuery(
        name = "StageEntity.removeByRequestStageIds",
        query = "DELETE FROM StageEntity stage WHERE stage.stageId = :stageId AND stage.requestId = :requestId")
})
public class StageEntity {

  @Basic
  @Column(name = "cluster_id", updatable = false, nullable = false)
  private Long clusterId = Long.valueOf(-1L);

  @Id
  @Column(name = "request_id", insertable = false, updatable = false, nullable = false)
  private Long requestId;

  @Id
  @Column(name = "stage_id", insertable = true, updatable = false, nullable = false)
  private Long stageId = 0L;

  @SuppressWarnings("serial")
  public static class StageEntityPK implements Serializable {

  	private Long requestId;
  	private Long stageId;

  }

  
  @Basic
  @Column(name = "skippable", nullable = false)
  private Integer skippable = Integer.valueOf(0);

  @Basic
  @Column(name = "supports_auto_skip_failure", nullable = false)
  private Integer supportsAutoSkipOnFailure = Integer.valueOf(0);

  @Basic
  @Column(name = "log_info")
  private String logInfo = "";

  @Basic
  @Column(name = "request_context")
  private String requestContext = "";

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "command_execution_type", nullable = false)
  private CommandExecutionType commandExecutionType = CommandExecutionType.STAGE;

  /**
   * On large clusters, this value can be in the 10,000's of kilobytes. During
   * an upgrade, all stages are loaded in memory for every request, which can
   * lead to an OOM. As a result, lazy load this since it's barely ever
   * requested or used.
   */
  @Column(name = "command_params")
  @Basic(fetch = FetchType.LAZY)
  private byte[] commandParamsStage;

  @Basic
  @Column(name = "host_params")
  private byte[] hostParamsStage;

  /**
   * This status informs if the advanced criteria for the stage success
   * as established at the time of stage creation has been accomplished or not
   *
   *  Status calculated by taking into account following
   *  a) {@link #roleSuccessCriterias}
   *  b) {@link #skippable}
   *  c) {@link HostRoleCommandEntity#autoSkipOnFailure}
   *  d) {@link HostRoleCommandEntity#status}
   *
   */
  @Column(name = "status",  nullable = false)
  @Enumerated(EnumType.STRING)
  private HostRoleStatus status = HostRoleStatus.PENDING;

  /**
   * This status informs if any of the underlying tasks
   * have faced any type of failures {@link HostRoleStatus#isFailedState()}
   *
   * Status calculated by only taking into account {@link HostRoleCommandEntity#status}
   *
   */
  @Column(name = "display_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private HostRoleStatus displayStatus = HostRoleStatus.PENDING;

  @ManyToOne
  @JoinColumn(name = "request_id", referencedColumnName = "request_id", nullable = false)
  private RequestEntity request;


  @OneToMany(mappedBy = "stage", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private Collection<HostRoleCommandEntity> hostRoleCommands;

  @OneToMany(mappedBy = "stage", cascade = CascadeType.REMOVE)
  private Collection<RoleSuccessCriteriaEntity> roleSuccessCriterias;

}
