package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.BlueprintConfigEntity.BlueprintConfigEntityPK;

/**
 * Represents a blueprint configuration.
 */
@javax.persistence.IdClass(BlueprintConfigEntityPK.class)
@Table(name = "blueprint_configuration")
@Entity
public class BlueprintConfigEntity {

  @Id
  @Column(name = "blueprint_name", nullable = false, insertable = false, updatable = false)
  private String blueprintName;

  @Id
  @Column(name = "type_name", nullable = false, insertable = true, updatable = false)
  private String type;

  /**
   * Composite primary key for BlueprintConfigEntity.
   */
  public static class BlueprintConfigEntityPK {

    @Id
    @Column(name = "blueprint_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String blueprintName;

    @Id
    @Column(name = "type_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String type;

  }
  
  @Column(name = "config_data", nullable = false, insertable = true, updatable = false)
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String configData;

  @Column(name = "config_attributes", nullable = true, insertable = true, updatable = false)
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String configAttributes;

  @ManyToOne
  @JoinColumn(name = "blueprint_name", referencedColumnName = "blueprint_name", nullable = false)
  private BlueprintEntity blueprint;

}
