package fr.an.tests.orika;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.an.tests.orika.domain.AEntity;
import fr.an.tests.orika.domain.BEntity;
import fr.an.tests.orika.dto.ADTO;

public class DtoConverterTest {

	@Test
	public void testMap() {
		// given
		AEntity a = createA(1, 2);
		// when
		ADTO res = DtoConverter.map(a, ADTO.class);
		// then
		Assert.assertEquals(a.getName(), res.name);
	}
	
	@Test
	public void testMapAsList() {
		// given
		AEntity a = createA(1, 2);
		List<AEntity> ls = Arrays.asList(a, createA(3, 4));
		// when
		List<ADTO> res = DtoConverter.mapAsList(ls, ADTO.class);
		// then
		Assert.assertEquals(res.size(), ls.size());
		ADTO res0 = res.get(0);
		Assert.assertEquals(a.getName(), res0.name);
	}

	private static AEntity createA(int id, int bId) {
		AEntity a = new AEntity();
		a.setId(id);
		a.setName("a" + id);;
		BEntity b = new BEntity();
		b.setId(bId);
		b.setName("b" + bId);
		a.setB(b);
		return a;
	}
}
