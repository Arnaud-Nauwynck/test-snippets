package fr.an.tests.hivemetastorejpa;

import javax.persistence.EntityManager;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
// @SpringBootTest //(classes = HiveMetastoreTestConfiguration.class)
@DataJpaTest
public abstract class AbstractHiveMetastoreJpaTest {

	@Autowired
	protected EntityManager em;
	
}
