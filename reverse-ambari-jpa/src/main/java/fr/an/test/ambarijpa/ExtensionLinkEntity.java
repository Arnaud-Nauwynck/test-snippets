package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * The {@link ExtensionLinkEntity} class is used to model the extensions linked to the stack.
 *
 * An extension version is like a stack version but it contains custom services.  Linking an extension
 * version to the current stack version allows the cluster to install the custom services contained in
 * the extension version.
 */
@Table(name = "extensionlink", uniqueConstraints = @UniqueConstraint(columnNames = {
		"stack_id", "extension_id" }))
@TableGenerator(name = "link_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "link_id_seq", initialValue = 0)
@NamedQueries({
    @NamedQuery(name = "ExtensionLinkEntity.findAll", query = "SELECT link FROM ExtensionLinkEntity link"),
    @NamedQuery(name = "ExtensionLinkEntity.findByStackAndExtensionName", query = "SELECT link FROM ExtensionLinkEntity link WHERE link.stack.stackName = :stackName AND link.stack.stackVersion = :stackVersion AND link.extension.extensionName = :extensionName"),
    @NamedQuery(name = "ExtensionLinkEntity.findByStackAndExtension", query = "SELECT link FROM ExtensionLinkEntity link WHERE link.stack.stackName = :stackName AND link.stack.stackVersion = :stackVersion AND link.extension.extensionName = :extensionName AND link.extension.extensionVersion = :extensionVersion"),
    @NamedQuery(name = "ExtensionLinkEntity.findByStack", query = "SELECT link FROM ExtensionLinkEntity link WHERE link.stack.stackName = :stackName AND link.stack.stackVersion = :stackVersion"),
    @NamedQuery(name = "ExtensionLinkEntity.findByExtension", query = "SELECT link FROM ExtensionLinkEntity link WHERE link.extension.extensionName = :extensionName AND link.extension.extensionVersion = :extensionVersion") })
@Entity
public class ExtensionLinkEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "link_id_generator")
  @Column(name = "link_id", nullable = false, updatable = false)
  private Long linkId;

  
  @OneToOne
  @JoinColumn(name = "stack_id", unique = false, nullable = false, insertable = true, updatable = false)
  private StackEntity stack;

  @OneToOne
  @JoinColumn(name = "extension_id", unique = false, nullable = false, insertable = true, updatable = false)
  private ExtensionEntity extension;


}
