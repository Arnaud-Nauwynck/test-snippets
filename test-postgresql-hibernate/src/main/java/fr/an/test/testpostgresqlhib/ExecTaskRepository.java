package fr.an.test.testpostgresqlhib;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecTaskRepository extends JpaRepository<ExecTaskEntity,Long> {

    public long countByStatus(ExecTaskStatus status);
    
}
