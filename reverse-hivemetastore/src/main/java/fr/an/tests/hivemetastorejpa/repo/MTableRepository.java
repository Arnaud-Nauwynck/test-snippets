package fr.an.tests.hivemetastorejpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.an.tests.hivemetastorejpa.domain.MTable;

public interface MTableRepository extends JpaRepository<MTable, Long> {

}
