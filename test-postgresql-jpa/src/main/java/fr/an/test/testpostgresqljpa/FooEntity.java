package fr.an.test.testpostgresqljpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

@Entity
@Table(name = "Foo")
@Data
public class FooEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "SEQ_FOO", allocationSize = 100)
    private Integer id;
    
    @Version
    private int version;

    @Column(name = "field_int")
    private int fieldInt;
    
//    @Column(name = "field_var_char20", length = 20)
//    private String fieldVarChar20;
//
//    @Column(name = "field_string", length = 4000)
//    private String fieldString;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<BarEntity> child;

}
