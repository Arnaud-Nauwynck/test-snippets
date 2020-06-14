package fr.an.tests.hivemetastorejpa.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MStringListValuePK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int stringListId;
		private int integerIdx;
	}
	
	@Entity
	@Table(name = "SKEWED_STRING_LIST_VALUES")
	@IdClass(MStringListValuePK.class)
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
