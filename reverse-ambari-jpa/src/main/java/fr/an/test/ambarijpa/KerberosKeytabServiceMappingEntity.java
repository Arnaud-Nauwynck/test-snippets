package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "kkp_mapping_service")
public class KerberosKeytabServiceMappingEntity {
  @Id
  @Column(name = "kkp_id", nullable = false, insertable = false, updatable = false)
  private  Long kerberosKeytabPrincipalId;

  @Id
  @Column(name = "service_name", nullable = false)
  private  String serviceName;

  @Id
  @Column(name = "component_name", nullable = false)
  private  String componentName;

  @ManyToOne
  @JoinColumn(name = "kkp_id")
  private KerberosKeytabPrincipalEntity kerberosKeytabPrincipalEntity;

}
