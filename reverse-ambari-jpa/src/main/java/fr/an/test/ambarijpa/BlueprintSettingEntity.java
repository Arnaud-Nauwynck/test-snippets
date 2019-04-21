package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * Represents a blueprint setting.
 */
@Table(name = "blueprint_setting", uniqueConstraints =
@UniqueConstraint(
        name = "UQ_blueprint_setting_name", columnNames = {"blueprint_name", "setting_name"}
  )
)

@TableGenerator(name = "blueprint_setting_id_generator",
        table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
        pkColumnValue = "blueprint_setting_id_seq", initialValue = 0)

@Entity
public class BlueprintSettingEntity {

  @Id
  @Column(name = "id", nullable = false, insertable = true, updatable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "blueprint_setting_id_generator")
  private long id;

  @Column(name = "blueprint_name", nullable = false, insertable = false, updatable = false)
  private String blueprintName;

  @Column(name = "setting_name", nullable = false, insertable = true, updatable = false)
  private String settingName;

  @Column(name = "setting_data", nullable = false, insertable = true, updatable = false)
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String settingData;

  @ManyToOne
  @JoinColumn(name = "blueprint_name", referencedColumnName = "blueprint_name", nullable = false)
  private BlueprintEntity blueprint;

}
