package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.state.SecurityType;


/**
 * Entity representing a Blueprint.
 */
@Table(name = "blueprint")
@NamedQuery(name = "allBlueprints",
    query = "SELECT blueprint FROM BlueprintEntity blueprint")
@Entity
public class BlueprintEntity {

  @Id
  @Column(name = "blueprint_name", nullable = false, insertable = true,
      updatable = false, unique = true, length = 100)
  private String blueprintName;

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "security_type", nullable = false, insertable = true, updatable = true)
  private SecurityType securityType = SecurityType.NONE;

  @Basic
  @Column(name = "security_descriptor_reference", nullable = true, insertable = true, updatable = true)
  private String securityDescriptorReference;


  @OneToOne
  @JoinColumn(name = "stack_id", unique = false, nullable = false, insertable = true, updatable = false)
  private StackEntity stack;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "blueprint")
  private Collection<HostGroupEntity> hostGroups;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "blueprint")
  private Collection<BlueprintConfigEntity> configurations;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "blueprint")
  private Collection<BlueprintSettingEntity> settings;

}
