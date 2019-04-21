package fr.an.test.ambarijpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Represents a Repository operation system type.
 */
@Entity
@Table(name = "repo_os")
@TableGenerator(name = "repo_os_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "repo_os_id_seq")
public class RepoOsEntity {
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "repo_os_id_generator")
	private Long id;

	@Column(name = "family")
	private String family;

	@Column(name = "ambari_managed", nullable = false)
	private short ambariManaged = 1;

	@OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "repoOs")
	private List<RepoDefinitionEntity> repoDefinitionEntities = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "repo_version_id", nullable = false)
	private RepositoryVersionEntity repositoryVersionEntity;

}
