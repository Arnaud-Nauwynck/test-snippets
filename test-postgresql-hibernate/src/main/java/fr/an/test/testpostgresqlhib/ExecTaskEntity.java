package fr.an.test.testpostgresqlhib;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "exec_task")
@Data
public class ExecTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "SEQ_EXECTASK", allocationSize = 100)
    private Long id;
    
    @Enumerated
    private ExecTaskStatus status;
    
    private Integer groupId;
    
}
