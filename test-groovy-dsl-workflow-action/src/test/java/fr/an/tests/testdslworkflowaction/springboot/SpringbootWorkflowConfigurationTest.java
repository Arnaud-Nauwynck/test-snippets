package fr.an.tests.testdslworkflowaction.springboot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionFactory;
import lombok.val;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=TestAppConfiguration.class)
public class SpringbootWorkflowConfigurationTest {
	
	@Autowired
	private AppParams appParams;
	
	@Autowired
	private AppParam2s appParam2s;
	
	@Test
	public void testYamlConfig() {
		val factory = new WorkflowActionFactory();
		String prop1 = appParams.getProp1();
		Assert.assertEquals("value1", prop1);
		List<Map<String,Object>> wfs = appParams.getWorkflows();
		List<AbstractWorkflowAction> actions = objToActions(wfs, factory); 
		checkWorkflowAction(actions.get(0));
	}
	
	@Test
	public void testYamlConfig2() {
		val factory = new WorkflowActionFactory();
		String prop1 = appParam2s.getProp1();
		Assert.assertEquals("value1", prop1);
		List<Map<String,Object>> wfs = appParam2s.getWorkflows();
		List<AbstractWorkflowAction> actions = objToActions(wfs, factory); 
		checkWorkflowAction(actions.get(0));
	}

	protected List<AbstractWorkflowAction> objToActions(List<Map<String,Object>> srcs, WorkflowActionFactory factory) {
		val res = new ArrayList<AbstractWorkflowAction>();
		for(val src : srcs) {
			res.add(objToAction(src, factory));
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> toList(Object src) {
		val res = new ArrayList<T>();
		if (!(src instanceof Map)) {
			throw new RuntimeException("expecting Map, with keys \"0\", \"1\".. ");
		}
		val srcItems = (Map<String,T>) src;
		for(int i = 0; ; i++) {
			String key = Integer.toString(i);
			val srcItem = (Map<String,T>) srcItems.get(key);
			if (srcItem == null) {
				break;
			}
			res.add((T) srcItem);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	protected AbstractWorkflowAction objToAction(Map<String,Object> src, WorkflowActionFactory factory) {
		AbstractWorkflowAction res;
		String type = (String) src.get("type");
		String name = (String) src.get("name");
		if (name == null) {
			name = "<name>";
		}
		switch(type) {
		case "parallel": {
			val branchItems = new LinkedHashMap<String,AbstractWorkflowAction>();
			List<Map<String,Object>> srcBranchItems = toList(src.get("branchItems"));
			for(val srcBranchItem: srcBranchItems) {
				val branchName = (String) srcBranchItem.get("branchName");
				val srcItem = (Map<String,Object>) srcBranchItem.get("item");
				val item = objToAction(srcItem, factory);
				branchItems.put(branchName, item);
			}
			res = factory.parallel(name, branchItems);
		} break;
		case "sequence": {
			List<Map<String,Object>> srcItems = toList(src.get("items"));
			val items = objToActions(srcItems, factory);
			res = factory.sequence(name, items);			
		} break;
		case "simple1": {
			String param = (String) src.get("param");
			res = factory.simple1(name, param);
		} break;
		case "simple2": {
			int intParam = toInt(src.get("intParam"), 0);
			res = factory.simple2(name, intParam);
		} break;
		default: 
			res = null;
		}
		return res;
	}

	private int toInt(Object src, int defaultValue) {
		int intParam = 0;
		if (src == null) {
			intParam = 0;
		} else if (src instanceof Number) {
			intParam = ((Number) src).intValue();
		} else if (src instanceof String) {
			intParam = Integer.parseInt((String) src);
		} else {
			intParam = 0;
			// unrecognized type
		}
		return intParam;
	}

	private void checkWorkflowAction(AbstractWorkflowAction wf) {
		val buffer = new ByteArrayOutputStream();
		try(val out = new PrintStream(buffer)) {
			wf.printIndent(out, 0);
		}
		String dumpText = buffer.toString();
		System.out.println("Wf:\n" + dumpText);
	}
}
