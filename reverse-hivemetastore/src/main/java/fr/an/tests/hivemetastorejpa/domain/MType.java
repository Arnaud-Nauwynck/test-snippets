package fr.an.tests.hivemetastorejpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Represents a Hive type
 *
 */
@Entity
@Table(name = "TYPES")
@Data
public class MType {

	@Id
    @Column(name = "TYPES_ID")
    private int typesId;
    
	@Column(name = "TYPE_NAME", length = 128)
	private String name;
	
	@Column(name = "TYPE1", length = 767)
	private String type1;
	
	@Column(name = "TYPE2", length = 767)
	private String type2;
	
	// private List<MFieldSchema> fields;
	@OneToMany(mappedBy = "typeName")
	private List<MTypeField> fields;

	@Entity
	@Table(name = "TYPE_FIELDS")
	@Data
	public static class MTypeField {
	
		@Id
		@Column(name = "TYPE_NAME", nullable = false)
		private String typeName;
		
		@Id
		@Column(name = "INTEGER_IDX", nullable = false)
		private int integerIdx;
				
		@Column(name = "COMMENT", length = 256)
		private String comment;
		
		@Column(name = "FIELD_NAME", length = 128, nullable = false)
		private String fieldName;

		@Column(name = "FIELD_TYPE", length = 767, nullable = false)
		private String fieldType;
		
	}
  
}
