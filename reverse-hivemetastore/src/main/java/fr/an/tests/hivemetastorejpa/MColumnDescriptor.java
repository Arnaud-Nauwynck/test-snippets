package fr.an.tests.hivemetastorejpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * MColumnDescriptor.
 * A wrapper around a list of columns.
 */
@Data
@Entity
@Table(name = "CDS")
public class MColumnDescriptor {

	@Id
	@Column(name = "CD_ID", nullable = false)
	private int cdId;
	
//	private List<MFieldSchema> cols;
	@OneToMany(mappedBy = "cd")
	private List<MFieldSchema> cols;
	
}
