package fr.an.tests;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import fr.an.tests.velocity.model.ItemRepository;

/**
 *
 */
public class ItemVelocityGenerator  {

	private VelocityEngine velocityEngine;
	private Map<String,Template> cachedTemplates = new HashMap<>();
	
	public ItemVelocityGenerator() {
		this.velocityEngine = new VelocityEngine();
		velocityEngine.init();
	}

	public String generateTemplate1(ItemRepository itemsRepository) {
		return generateTemplate("/templates/template1.vm", itemsRepository);
	}
	
	public String generateTemplate(String templateName, ItemRepository itemsRepository) {
		Template template = getTemplate(templateName);

		VelocityContext context = new VelocityContext();
		context.put("repo", itemsRepository);
		context.put("name", "World");
		     
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}

	private Template getTemplate(String templateName) {
		Template res = this.cachedTemplates.get(templateName);
		if (res == null) {
			res = this.velocityEngine.getTemplate("src/main/resources/" + templateName);
			this.cachedTemplates.put(templateName, res);
		}
		return res;
	}
	
}
