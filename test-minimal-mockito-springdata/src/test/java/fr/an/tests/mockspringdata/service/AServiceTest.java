package fr.an.tests.mockspringdata.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import fr.an.tests.mockspringdata.domain.AEntity;
import fr.an.tests.mockspringdata.domain.BEntity;
import fr.an.tests.mockspringdata.dto.ADTO;
import fr.an.tests.mockspringdata.repository.ARepo;
import fr.an.tests.mockspringdata.service.AService;
import fr.an.tests.mockspringdata.service.DtoConverter;

@RunWith(MockitoJUnitRunner.class)
public class AServiceTest {

	BEntity b2 = new BEntity(2, "b2");
	AEntity a1 = new AEntity(1, "a1", b2); 
	AEntity a2 = new AEntity(1, "a2", b2); 

	@InjectMocks
	AService sut = new AService();
	
	@Mock
	ARepo aRepo;

	@Spy
	DtoConverter converter = new DtoConverter(); 
	
	@Test
	public void testFindByNameLike() {
		// given
		Mockito.when(aRepo.findByNameLike("a%")).thenReturn(Arrays.asList(a1, a2));
		// when
		List<ADTO> res = sut.findByNameLike("a%");
		// then
		Assert.assertEquals(2, res.size());
		Assert.assertEquals("a1", res.get(0).getName());
		Assert.assertEquals("a2", res.get(1).getName());
	}

}
