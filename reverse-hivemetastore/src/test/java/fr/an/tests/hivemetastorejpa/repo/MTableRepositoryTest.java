package fr.an.tests.hivemetastorejpa.repo;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.an.tests.hivemetastorejpa.AbstractHiveMetastoreJpaTest;
import fr.an.tests.hivemetastorejpa.domain.MTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MTableRepositoryTest extends AbstractHiveMetastoreJpaTest {

	@Autowired
	private MTableRepository tableRepo;
	
	@Test
	public void test() {
		List<MTable> tables = tableRepo.findAll();
		// select mtable0_.tbl_id as tbl_id1_49_, mtable0_.create_time as create_t2_49_, mtable0_.db_id as db_id13_49_, mtable0_.last_access_time as last_acc3_49_, mtable0_.owner as owner4_49_, mtable0_.owner_type as owner_ty5_49_, mtable0_.retention as retentio6_49_, mtable0_.is_rewrite_enabled as is_rewri7_49_, mtable0_.sd_id as sd_id14_49_, mtable0_.tbl_name as tbl_name8_49_, mtable0_.tbl_type as tbl_type9_49_, mtable0_.view_expanded_text as view_ex10_49_, mtable0_.view_original_text as view_or11_49_, mtable0_.write_id as write_i12_49_ from tbls mtable0_
		log.info("tables " + tables.size());
	}

}
