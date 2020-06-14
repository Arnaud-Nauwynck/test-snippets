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
import javax.persistence.UniqueConstraint;

import fr.an.tests.hivemetastorejpa.domain.MFieldSchema.MFieldSchemaPK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represent a column or a type of a table or object
 */
@Entity
@Table(name = "COLUMNS_V2", 
	uniqueConstraints=@UniqueConstraint( name = "COLUMNS_V2_pkey", columnNames = {"CD_ID", "COLUMN_NAME" })
)
@Data
@IdClass(MFieldSchemaPK.class)
public class MFieldSchema {

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MFieldSchemaPK implements Serializable {
		private static final long serialVersionUID = 1L;

		private int cd;
	    private int integerIdx;
	}
	
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CD_ID", nullable = false)
	private MColumnDescriptor cd;
	// private int cdId;

	@Id
	@Column(name = "INTEGER_IDX", nullable = false)
    private int integerIdx;

    @Column(name = "COLUMN_NAME", length = 767, nullable = false)
    private String name;
    
    @Column(name = "TYPE_NAME")
    private String type;

    @Column(name = "COMMENT", length = 4000)
    private String comment;

}
