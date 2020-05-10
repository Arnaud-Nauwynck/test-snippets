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
 * It represents data structure of string list.
 *
 */
@Entity
@Table(name = "SKEWED_STRING_LIST")
@Data
public class MStringList {

	@Id
	@Column(name = "STRING_LIST_ID")
	private int stringListId;

	// private List<String> internalList;
	@OneToMany(mappedBy = "stringListId")
	private List<MStringListValue> internalList;

	@Entity
	@Table(name = "SKEWED_STRING_LIST_VALUES")
	@Data
	public static class MStringListValue {

		@Id
		@Column(name = "STRING_LIST_ID", nullable = false)
		private int stringListId;

		@Id
		@Column(name = "INTEGER_IDX", nullable = false)
		private int integerIdx;

		@Column(name = "STRING_LIST_VALUE", length = 256)
		private String stringListValue;

	}

}
