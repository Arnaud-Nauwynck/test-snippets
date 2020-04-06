package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * The {@link StackEntity} class is used to model an alert that needs
 * to run in the system. Each received alert from an agent will essentially be
 * an instance of this template.
 */
@Entity
@Table(name = "stack", uniqueConstraints = @UniqueConstraint(columnNames = {
    "stack_name", "stack_version" }))
@TableGenerator(name = "stack_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "stack_id_seq", initialValue = 0)
@NamedQueries({
    @NamedQuery(name = "StackEntity.findAll", query = "SELECT stack FROM StackEntity stack"),
    @NamedQuery(name = "StackEntity.findByMpack", query = "SELECT stack FROM StackEntity stack where stack.mpackId = :mpackId"),
    @NamedQuery(name = "StackEntity.findByNameAndVersion", query = "SELECT stack FROM StackEntity stack WHERE stack.stackName = :stackName AND stack.stackVersion = :stackVersion",
                hints = {
                  @QueryHint(name = "eclipselink.query-results-cache", value = "true"),
                  @QueryHint(name = "eclipselink.query-results-cache.size", value = "100")
                })
})
public class StackEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "stack_id_generator")
  @Column(name = "stack_id", nullable = false, updatable = false)
  private Long stackId;

  @Column(name = "stack_name", length = 255, nullable = false)
  private String stackName;

  @Column(name = "stack_version", length = 255, nullable = false)
  private String stackVersion;

  @Column(name = "mpack_id")
  private Long mpackId;

}