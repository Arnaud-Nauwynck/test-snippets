package fr.an.test.ambarijpa;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import fr.an.test.ambarijpa.state.GroupType;

@Entity
@Table(name = "groups", uniqueConstraints = { @UniqueConstraint(columnNames = { "group_name", "ldap_group" }) })
@TableGenerator(name = "group_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "group_id_seq", initialValue = 1)
@NamedQueries({
		@NamedQuery(name = "groupByName", query = "SELECT group_entity FROM GroupEntity group_entity where lower(group_entity.groupName)=:groupname") })
public class GroupEntity {

	@Id
	@Column(name = "group_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "group_id_generator")
	private Integer groupId;

	
	@Column(name = "group_name")
	private String groupName;

	@Column(name = "ldap_group")
	private Integer ldapGroup = 0;

	@Column(name = "group_type")
	@Enumerated(EnumType.STRING)
	@Basic
	private GroupType groupType = GroupType.LOCAL;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
	private Set<MemberEntity> memberEntities;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "principal_id", referencedColumnName = "principal_id", nullable = false), })
	private PrincipalEntity principal;

}
