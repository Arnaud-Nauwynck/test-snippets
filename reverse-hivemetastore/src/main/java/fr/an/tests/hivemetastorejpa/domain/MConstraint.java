package fr.an.tests.hivemetastorejpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.an.tests.hivemetastorejpa.domain.MConstraint.MConstraintPK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Representing a row in the KEY_CONSTRAINTS table. */
@Entity
@Table(name = "KEY_CONSTRAINTS")
//   CONSTRAINT CONSTRAINTS_PK PRIMARY KEY ("PARENT_TBL_ID", "CONSTRAINT_NAME", "POSITION")
@IdClass(MConstraintPK.class)
@Data
public class MConstraint {

	// 0 - Primary Key
	// 1 - PK-FK relationship
	// 2 - Unique Constraint
	// 3 - Not Null Constraint
	public final static int PRIMARY_KEY_CONSTRAINT = 0;
	public final static int FOREIGN_KEY_CONSTRAINT = 1;
	public final static int UNIQUE_CONSTRAINT = 2;
	public final static int NOT_NULL_CONSTRAINT = 3;
	public final static int DEFAULT_CONSTRAINT = 4;
	public final static int CHECK_CONSTRAINT = 5;

    @Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MConstraintPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int parentTable;
		private String constraintName;
		private int position;

		public String toString() {
			return String.format("%s:%s:%d", parentTable, constraintName, position);
		}
    }
    
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_TBL_ID", nullable = false)
	private MTable parentTable; // Primary key of KEY_CONSTARINTS

	@Id
	@Column(name = "CONSTRAINT_NAME", length = 400, nullable = false)
	private String constraintName; // Primary key of KEY_CONSTARINTS

	@Id
	@Column(name = "POSITION", nullable = false)
	private int position; // Primary key of KEY_CONSTARINTS

	
	
	@Column(name = "CONSTRAINT_TYPE", nullable = false)
	private int constraintType;

	@Column(name = "DELETE_RULE")
	private Integer deleteRule;

	@Column(name = "UPDATE_RULE")
	private Integer updateRule;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHILD_TBL_ID")
	private MTable childTable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_CD_ID")
	private MColumnDescriptor parentColumn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHILD_CD_ID")
	private MColumnDescriptor childColumn;

	@Column(name = "CHILD_INTEGER_IDX")
	private Integer childIntegerIndex;

	@Column(name = "PARENT_INTEGER_IDX", nullable = false)
	private Integer parentIntegerIndex;

	@Column(name = "ENABLE_VALIDATE_RELY", nullable = false)
	private int enableValidateRely;

	@Column(name = "DEFAULT_VALUE", length = 400)
	private String defaultValue;


}
