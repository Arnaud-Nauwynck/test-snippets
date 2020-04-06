package fr.an.test.ambarijpa;

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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.HostRoleStatus;
import fr.an.test.ambarijpa.state.RoleCommand;

@Entity
@Table(name = "host_role_command"
       , indexes = {
           @Index(name = "idx_hrc_request_id", columnList = "request_id")
         , @Index(name = "idx_hrc_status_role", columnList = "status, role")
       })
@TableGenerator(name = "host_role_command_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
    , pkColumnValue = "host_role_command_id_seq"
    , initialValue = 1
)
@NamedQueries({
    @NamedQuery(
        name = "HostRoleCommandEntity.findTaskIdsByRequestStageIds",
        query = "SELECT command.taskId FROM HostRoleCommandEntity command WHERE command.stageId = :stageId AND command.requestId = :requestId"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findCountByCommandStatuses",
        query = "SELECT COUNT(command.taskId) FROM HostRoleCommandEntity command WHERE command.status IN :statuses"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findByRequestIdAndStatuses",
        query = "SELECT task FROM HostRoleCommandEntity task WHERE task.requestId=:requestId AND task.status IN :statuses ORDER BY task.taskId ASC"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findTasksByStatusesOrderByIdDesc",
        query = "SELECT task FROM HostRoleCommandEntity task WHERE task.requestId = :requestId AND task.status IN :statuses ORDER BY task.taskId DESC"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findNumTasksAlreadyRanInStage",
        query = "SELECT COUNT(task.taskId) FROM HostRoleCommandEntity task WHERE task.requestId = :requestId AND task.taskId > :taskId AND task.stageId > :stageId AND task.status NOT IN :statuses"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findByCommandStatuses",
        query = "SELECT command FROM HostRoleCommandEntity command WHERE command.status IN :statuses ORDER BY command.requestId, command.stageId"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findByHostId",
        query = "SELECT command FROM HostRoleCommandEntity command WHERE command.hostId=:hostId"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findByHostRole",
        query = "SELECT command FROM HostRoleCommandEntity command WHERE command.hostEntity.hostName=:hostName AND command.requestId=:requestId AND command.stageId=:stageId AND command.role=:role ORDER BY command.taskId"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findByHostRoleNullHost",
        query = "SELECT command FROM HostRoleCommandEntity command WHERE command.hostEntity IS NULL AND command.requestId=:requestId AND command.stageId=:stageId AND command.role=:role"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findByStatusBetweenStages",
        query = "SELECT command FROM HostRoleCommandEntity command WHERE command.requestId = :requestId AND command.stageId >= :minStageId AND command.stageId <= :maxStageId AND command.status = :status"),
    @NamedQuery(
        name = "HostRoleCommandEntity.updateAutoSkipExcludeRoleCommand",
        query = "UPDATE HostRoleCommandEntity command SET command.autoSkipOnFailure = :autoSkipOnFailure WHERE command.requestId = :requestId AND command.roleCommand <> :roleCommand"),
    @NamedQuery(
        name = "HostRoleCommandEntity.updateAutoSkipForRoleCommand",
        query = "UPDATE HostRoleCommandEntity command SET command.autoSkipOnFailure = :autoSkipOnFailure WHERE command.requestId = :requestId AND command.roleCommand = :roleCommand"),
    @NamedQuery(
        name = "HostRoleCommandEntity.removeByTaskIds",
        query = "DELETE FROM HostRoleCommandEntity command WHERE command.taskId IN :taskIds"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findHostsByCommandStatus",
        query = "SELECT DISTINCT(host.hostName) FROM HostRoleCommandEntity command, HostEntity host WHERE (command.requestId >= :iLowestRequestIdInProgress AND command.requestId <= :iHighestRequestIdInProgress) AND command.status IN :statuses AND command.hostId = host.hostId AND host.hostName IS NOT NULL"),
    @NamedQuery(
        name = "HostRoleCommandEntity.getBlockingHostsForRequest",
        query = "SELECT DISTINCT(host.hostName) FROM HostRoleCommandEntity command, HostEntity host WHERE command.requestId >= :lowerRequestIdInclusive AND command.requestId < :upperRequestIdExclusive AND command.status IN :statuses AND command.isBackgroundCommand=0 AND command.hostId = host.hostId AND host.hostName IS NOT NULL"),
    @NamedQuery(
        name = "HostRoleCommandEntity.findLatestServiceChecksByRole",
        query = "SELECT NEW org.apache.ambari.server.orm.dao.HostRoleCommandDAO.LastServiceCheckDTO(command.role, MAX(command.endTime)) FROM HostRoleCommandEntity command WHERE command.roleCommand = :roleCommand AND command.endTime > 0 AND command.stage.clusterId = :clusterId GROUP BY command.role ORDER BY command.role ASC"),
    @NamedQuery(
      name = "HostRoleCommandEntity.findByRequestId",
      query = "SELECT command FROM HostRoleCommandEntity command WHERE command.requestId = :requestId ORDER BY command.taskId")
})
public class HostRoleCommandEntity {

  private static int MAX_COMMAND_DETAIL_LENGTH = 250;

  @Column(name = "task_id")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_role_command_id_generator")
  private Long taskId;

  @Column(name = "request_id", insertable = false, updatable = false, nullable = false)
  @Basic
  private Long requestId;

  @Column(name = "stage_id", insertable = false, updatable = false, nullable = false)
  @Basic
  private Long stageId;

  @Column(name = "host_id", insertable = false, updatable = false, nullable = true)
  @Basic
  private Long hostId;

  @Column(name = "role")
  private String role;

  @Column(name = "event", length = 32000)
  @Basic
  @Lob
  private String event = "";

  @Column(name = "exitcode", nullable = false)
  @Basic
  private Integer exitcode = 0;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private HostRoleStatus status = HostRoleStatus.PENDING;

  @Column(name = "std_error")
  @Lob
  @Basic
  private byte[] stdError = new byte[0];

  @Column(name = "std_out")
  @Lob
  @Basic
  private byte[] stdOut = new byte[0];

  @Column(name = "output_log")
  @Basic
  private String outputLog = null;

  @Column(name = "error_log")
  @Basic
  private String errorLog = null;


  @Column(name = "structured_out")
  @Lob
  @Basic
  private byte[] structuredOut = new byte[0];

  @Basic
  @Column(name = "start_time", nullable = false)
  private Long startTime = -1L;

  /**
   * Because the startTime is allowed to be overwritten, introduced a new column for the original start time.
   */
  @Basic
  @Column(name = "original_start_time", nullable = false)
  private Long originalStartTime = -1L;

  @Basic
  @Column(name = "end_time", nullable = false)
  private Long endTime = -1L;

  @Basic
  @Column(name = "last_attempt_time", nullable = false)
  private Long lastAttemptTime = -1L;

  @Basic
  @Column(name = "attempt_count", nullable = false)
  private Short attemptCount = 0;

  @Column(name = "retry_allowed", nullable = false)
  private Integer retryAllowed = Integer.valueOf(0);

  /**
   * If the command fails and is skippable, then this will instruct the
   * scheduler to skip the command.
   */
  @Column(name = "auto_skip_on_failure", nullable = false)
  private Integer autoSkipOnFailure = Integer.valueOf(0);

  // This is really command type as well as name
  @Column(name = "role_command")
  @Enumerated(EnumType.STRING)
  private RoleCommand roleCommand;

  // A readable description of the command
  @Column(name = "command_detail")
  @Basic
  private String commandDetail;

  // An optional property that can be used for setting the displayName for operations window
  @Column(name = "ops_display_name")
  @Basic
  private String opsDisplayName;

  // When command type id CUSTOM_COMMAND and CUSTOM_ACTION this is the name
  @Column(name = "custom_command_name")
  @Basic
  private String customCommandName;

  @OneToOne(mappedBy = "hostRoleCommand", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private ExecutionCommandEntity executionCommand;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumns({@JoinColumn(name = "request_id", referencedColumnName = "request_id", nullable = false), @JoinColumn(name = "stage_id", referencedColumnName = "stage_id", nullable = false)})
  private StageEntity stage;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "host_id", referencedColumnName = "host_id", nullable = true)
  private HostEntity hostEntity;

  @OneToOne(mappedBy = "hostRoleCommandEntity", cascade = CascadeType.REMOVE)
  private TopologyLogicalTaskEntity topologyLogicalTaskEntity;

  @Basic
  @Column(name = "is_background", nullable = false)
  private short isBackgroundCommand = 0;

}
