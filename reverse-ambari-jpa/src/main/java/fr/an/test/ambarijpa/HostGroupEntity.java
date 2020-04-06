package fr.an.test.ambarijpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.an.test.ambarijpa.HostGroupEntity.HostGroupEntityPK;

/**
 * Represents a Host Group which is embedded in a Blueprint.
 */
@javax.persistence.IdClass(HostGroupEntityPK.class)
@Table(name = "hostgroup")
@Entity
public class HostGroupEntity {

  @Id
  @Column(name = "blueprint_name", nullable = false, insertable = false, updatable = false)
  private String blueprintName;

  @Id
  @Column(name = "name", nullable = false, insertable = true, updatable = false)
  private String name;

  /**
   * Composite primary key for HostGroupEntity.
   */
  public static class HostGroupEntityPK {

    @Id
    @Column(name = "blueprint_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String blueprintName;

    @Id
    @Column(name = "name", nullable = false, insertable = true, updatable = false, length = 100)
    private String name;

  }

  
  @Column
  @Basic
  private String cardinality = "NOT SPECIFIED";

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "hostGroup")
  private Collection<HostGroupComponentEntity> components;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "hostGroup")
  private Collection<HostGroupConfigEntity> configurations;

  @ManyToOne
  @JoinColumn(name = "blueprint_name", referencedColumnName = "blueprint_name", nullable = false)
  private BlueprintEntity blueprint;

}
