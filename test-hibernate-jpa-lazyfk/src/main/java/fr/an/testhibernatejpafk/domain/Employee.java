package fr.an.testhibernatejpafk.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Employee {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="employees_seq")	
	@SequenceGenerator(name="employees_seq", sequenceName="employees_seq", allocationSize=10)
	@Getter
	private int id;
	
	@Version
	private int version;
	
	@Column(name="first_name")
	@Getter @Setter
	private String firstName;
	
	@Column(name="last_name")
	@Getter @Setter
	private String lastName;
	
	@Getter @Setter
	private String email;
	
	@Getter @Setter
	private String address;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="department_id")
	@Getter @Setter
	private Department department;

	/** redundant with department.id */
	@Column(name="department_id", insertable=false, updatable=false)
	@Getter @Setter(value=AccessLevel.PROTECTED)
	private Integer departmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="manager_id")
	@Getter @Setter
	private Employee manager;

	/** redundant with manager.id */
	@Column(name="manager_id", insertable=false, updatable=false)
	@Getter @Setter(value=AccessLevel.PROTECTED)
	private Integer managerId;
	
	
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
		Employee other = (Employee) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
