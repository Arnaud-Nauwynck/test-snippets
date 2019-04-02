package fr.an.test.ambarijpa;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import fr.an.test.ambarijpa.RoleSuccessCriteriaEntity.RoleSuccessCriteriaEntityPK;

@IdClass(RoleSuccessCriteriaEntityPK.class)
@Table(name = "role_success_criteria")
@Entity
@NamedQueries({
  @NamedQuery(name = "RoleSuccessCriteriaEntity.removeByRequestStageIds", query = "DELETE FROM RoleSuccessCriteriaEntity criteria WHERE criteria.stageId = :stageId AND criteria.requestId = :requestId")
})
public class RoleSuccessCriteriaEntity {

  @Id
  @Column(name = "request_id", insertable = false, updatable = false, nullable = false)
  private Long requestId;

  @Id
  @Column(name = "stage_id", insertable = false, updatable = false, nullable = false)
  private Long stageId;

  @Id
  @Column(name = "role")
  private String role;

  @SuppressWarnings("serial")
  public static class RoleSuccessCriteriaEntityPK implements Serializable {

  	@Id
  	@Column(name = "request_id")
  	private Long requestId;

  	@Id
  	@Column(name = "stage_id")
  	private Long stageId;

  	@Column(name = "role")
  	@Id
  	private String role;

  }

  
  @Basic
  @Column(name = "success_factor", nullable = false)
  private Double successFactor = 1d;

  @ManyToOne
  @JoinColumns({@JoinColumn(name = "request_id", referencedColumnName = "request_id", nullable = false), @JoinColumn(name = "stage_id", referencedColumnName = "stage_id", nullable = false)})
  private StageEntity stage;

}
