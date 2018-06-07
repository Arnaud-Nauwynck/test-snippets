package fr.an.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.an.tests.velocity.model.Item;
import fr.an.tests.velocity.model.ItemRepository;

public class ItemVelocityGeneratorTest {

	ItemVelocityGenerator sut = new ItemVelocityGenerator(); 

	static ItemRepository repo;
	
	@BeforeClass
	public static void prepare() {
		repo = new ItemRepository();
		repo.add(item("id1", "type1", "key1", "value1", "key2", "value1"));
		repo.add(item("id2", "type1", "key1", "value2", "key2", "value1"));
		repo.add(item("id3", "type2", "key1", "value1", "key3", "value1"));
		repo.add(item("id4", "type2", "key1", "value2", "key3", "value2"));
	}
	
	@Test
	public void testGenerateTemplate1() {
		String res = sut.generateTemplate1(repo);
		System.out.println(res);
	}
	
	protected static Item item(String id, String type, Object... keyValues) {
		Map<String,Object> props = new HashMap<>();
		int len = keyValues.length;
		for(int i = 0; i < len; i+=2) {
			String key = (String) keyValues[i];
			Object value = keyValues[i+1];
			props.put(key, value);
		}
		return new Item(id, type, props);
	}
}
