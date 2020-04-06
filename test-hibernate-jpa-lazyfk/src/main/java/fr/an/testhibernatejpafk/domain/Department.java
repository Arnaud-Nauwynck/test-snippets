package fr.an.testhibernatejpafk.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Department {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="departments_seq")
	@SequenceGenerator(name="departments_seq", sequenceName="departments_seq", allocationSize=1)
	@Getter
	// @Setter(AccessLevel.PRIVATE)
	private int id;
	
	@Version
	private int version;

	@Column(name="department_name")
	@Getter @Setter
	private String name;
	
	@OneToMany(mappedBy="department", fetch = FetchType.LAZY)
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private List<Employee> employees = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name="dept_manager_id")
	@Getter @Setter
	private Employee deptManager;

	/** redundant with deptManager.id */
	@Column(name="dept_manager_id", insertable=false, updatable=false)
	@Getter
	@Setter(value=AccessLevel.PROTECTED)
	private Integer deptManagerId;
	
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
