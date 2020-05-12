package fr.an.tests.hivemetastorejpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Representing a row in the KEY_CONSTRAINTS table. */
@Entity
@Table(name = "KEY_CONSTRAINTS")
//   CONSTRAINT CONSTRAINTS_PK PRIMARY KEY ("PARENT_TBL_ID", "CONSTRAINT_NAME", "POSITION")
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

	@Id
	@ManyToOne
	@Column(name = "PARENT_TBL_ID", nullable = false)
	private MTable parentTable; // Primary key of KEY_CONSTARINTS

	@Id
	@Column(name = "CONSTRAINT_NAME", length = 400, nullable = false)
	private String constraintName; // Primary key of KEY_CONSTARINTS

	@Id
	@Column(name = "POSITION", nullable = false)
	private int position; // Primary key of KEY_CONSTARINTS

	@Data
	@AllArgsConstructor @NoArgsConstructor
	public static class PK implements Serializable {

		public MTable.PK parentTable;
		public String constraintName;
		public int position;

		public String toString() {
			return String.format("%s:%s:%d", parentTable.id, constraintName, position);
		}

	}

	
	
	@Column(name = "CONSTRAINT_TYPE", nullable = false)
	private int constraintType;

	@Column(name = "DELETE_RULE")
	private Integer deleteRule;

	@Column(name = "UPDATE_RULE")
	private Integer updateRule;

	@ManyToOne
	@Column(name = "CHILD_TBL_ID")
	private MTable childTable;

	@ManyToOne
	@Column(name = "PARENT_CD_ID")
	private MColumnDescriptor parentColumn;

	@ManyToOne
	@Column(name = "CHILD_CD_ID")
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
