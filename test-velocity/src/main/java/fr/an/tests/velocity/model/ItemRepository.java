package fr.an.tests.velocity.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ItemRepository {

	public final Map<String,Item> itemByIds = new HashMap<>();

	public ItemRepository() {
	}
	
	public Map<String,Item> getItemByIds() {
		return itemByIds;
	}

	public Collection<Item> items() {
		return itemByIds.values();
	}

	public Item findById(String id) {
		return itemByIds.get(id);
	}
	
	public void add(Item item) {
		itemByIds.put(item.getId(), item);
	}
	
}
