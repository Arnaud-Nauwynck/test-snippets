package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

/**
 * Represent a column or a type of a table or object
 */
@Entity
@Table(name = "COLUMNS_V2", 
	uniqueConstraints=@UniqueConstraint( name = "COLUMNS_V2_pkey", columnNames = {"CD_ID", "COLUMN_NAME" })
)
@Data
public class MFieldSchema {

	@Id
	@ManyToOne
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
