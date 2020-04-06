package fr.an.test.testpostgresqljpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

@Entity
@Table(name = "bar")
@Data
public class BarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "SEQ_FOO", allocationSize = 100)
    private Integer id;
    
    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private FooEntity parent;
    
    @Column(name = "field_int")
    private int fieldInt;
    
}
