package fr.an.tests.mockspringdata.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import fr.an.tests.mockspringdata.JpaDataRepositoryTestHelper;
import fr.an.tests.mockspringdata.domain.AEntity;
import fr.an.tests.mockspringdata.domain.BEntity;
import fr.an.tests.mockspringdata.dto.ADTO;
import fr.an.tests.mockspringdata.repository.ARepo;

public class MinimalJpaDataNoSpring_AServiceTest extends Mockito {

	@InjectMocks
	AService sut = new AService();
	
	// user-defined mock ... relying on explicit spring-data
	ARepo aRepo;

	@Spy
	DtoConverter converter = new DtoConverter(); 
	
	static JpaDataRepositoryTestHelper jpaHelper = new JpaDataRepositoryTestHelper();
	EntityManager em;
	
	@Before
	public void setup() {
		// do not start springboot here... only hibernate(jpa) + h2 +... custom used-defined spring-data repository !!
		// start hibernate+jpa+h2
		this.em = jpaHelper.createEM();
		// create spring-data repository, but WITHOUT starting spring context!
		JpaRepositoryFactory repoFactory = new JpaRepositoryFactory(em);
		repoFactory.setRepositoryBaseClass(SimpleJpaRepository.class);
		aRepo = repoFactory.getRepository(ARepo.class);
		
		// inject stdandard @Mock and @Spy in sut
		MockitoAnnotations.initMocks(this);
		// HACK ... inject custom created "spy" (delegating to mock in-memory em)
		sut.setARepo(aRepo); // not injected by mockito!

		// explicit create transaction!
		em.getTransaction().begin();
	}

	@After
	public void after() {
		em.getTransaction().rollback();
	}

	@Test
	public void testFindByNameLike() {
		// insert some data..
		BEntity b2 = new BEntity(0, "b2");
		em.persist(b2);
		AEntity a1 = new AEntity(0, "a1", b2);
		em.persist(a1);
		int a1Id = a1.getId();
		AEntity a2 = new AEntity(0, "a2", b2);
		em.persist(a2);
		AEntity a3 = new AEntity(0, "xa3", b2);
		em.persist(a3);
		em.flush();
		
		// when
		AEntity a1Check = aRepo.getOne(a1Id);
		// when
		Assert.assertSame(a1, a1Check);
		
		// when
		List<AEntity> lsACheck = aRepo.findAll();
		// then
		Assert.assertEquals(3, lsACheck.size());

		// when
		List<AEntity> lsALikeCheck = aRepo.findByNameLike("a%");
		// then
		Assert.assertEquals(2, lsALikeCheck.size());
		
		
		// ... Mockito.when(aRepo.findByNameLike("a%")).thenReturn(Arrays.asList(a1, a2));
		// when
		List<ADTO> res = sut.findByNameLike("a%");
		// then
		Assert.assertEquals(2, res.size());
		Assert.assertEquals(a1.getName(), res.get(0).getName());
		Assert.assertEquals(a2.getName(), res.get(1).getName());
	}

}
