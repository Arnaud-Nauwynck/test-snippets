package fr.an.tests.eclipselink.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import fr.an.persistence.OneToMany;
import lombok.Data;

@Data // lombok getter, setter  ... override hashcode using id field!
@Entity
@Table(name="departments")
public class Department {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="departments_seq")
	@SequenceGenerator(name="departments_seq", sequenceName="departments_seq", allocationSize=1)
	@Column(name="department_id")
	private int id;
	
	@Version
	private int version;

	@Column(name="department_name")
	private String name;
	
	@OneToMany(mappedBy="department")
	private List<Employee> employees;
	
	@ManyToOne
	@JoinColumn(name="manager_id")
	private Employee deptManager;

	
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
