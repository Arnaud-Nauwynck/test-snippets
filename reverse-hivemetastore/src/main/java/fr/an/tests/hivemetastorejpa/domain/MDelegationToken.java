package fr.an.tests.hivemetastorejpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "DELEGATION_TOKENS")
@Data
public class MDelegationToken {

  @Id
  @Column(name = "TOKEN_IDENT", length = 767, nullable = false)
  private String tokenIdentifier;

  @Column(name = "TOKEN", length = 767)
  private String tokenStr;

}
