package fr.an.tests.testdslworkflowaction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.junit.Test;

import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.ParallelWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.SequenceWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.Simple1WorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.Simple2WorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionBuilder;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionFactory;
import fr.an.tests.testdslworkflowaction.groovydsl.WorkflowActionDslHelper;
import lombok.val;

/**
 * several ways to build following workflow:
 * 
 * <PRE>
 * wf: Parallel 3
 * branch [branch0]
 *  p1_branch0: Sequence 2
 *   [0]
 *    p1_branch0_item0: Simple1{ stringParam:param1 }
 *   [1]
 *    p1_branch0_item0: Simple2{ intParam:1 }
 * branch [branch1]
 *  p1_branch1: Sequence 2
 *   [0]
 *    p1_branch1_item0: Simple1{ stringParam:param2 }
 *   [1]
 *    p1_branch0_item1: Simple2{ intParam:2 }
 * branch [branch2]
 *  p1_branch2: Simple2{ intParam:3 }
 * </PRE>
 */
public class WorkflowsTest {

	@Test
	public void testNew() {
		val p1Items = new LinkedHashMap<String,AbstractWorkflowAction>();
		{ // p1_branch0
			val p1_branch0_items = new ArrayList<AbstractWorkflowAction>();
			p1_branch0_items.add(new Simple1WorkflowAction("p1_branch0_item0", "param1"));
			p1_branch0_items.add(new Simple2WorkflowAction("p1_branch0_item0", 1));
			val p1_branch0 = new SequenceWorkflowAction("p1_branch0", p1_branch0_items);
			p1Items.put("branch0", p1_branch0);
		}
		{ // p1_branch1
			val p1_branch1_items = new ArrayList<AbstractWorkflowAction>();
			p1_branch1_items.add(new Simple1WorkflowAction("p1_branch1_item0", "param2"));
			p1_branch1_items.add(new Simple2WorkflowAction("p1_branch0_item1", 2));
			val p1_branch1 = new SequenceWorkflowAction("p1_branch1", p1_branch1_items);
			p1Items.put("branch1", p1_branch1);
		}
		{
			val p1_branch2 = new Simple2WorkflowAction("p1_branch2", 3);
			p1Items.put("branch2", p1_branch2);
		}
		val wf = new ParallelWorkflowAction("wf", p1Items);
		
		checkWorkflowAction(wf);
	}

	@Test
	public void testFactory() {
		val factory = new WorkflowActionFactory();
		
		val p1Items = new LinkedHashMap<String,AbstractWorkflowAction>();
		{ // p1_branch0
			val p1_branch0_items = new ArrayList<AbstractWorkflowAction>();
			p1_branch0_items.add(factory.simple1("p1_branch0_item0", "param1"));
			p1_branch0_items.add(factory.simple2("p1_branch0_item0", 1));
			val p1_branch0 = new SequenceWorkflowAction("p1_branch0", p1_branch0_items);
			p1Items.put("branch0", p1_branch0);
		}
		{ // p1_branch1
			val p1_branch1_items = new ArrayList<AbstractWorkflowAction>();
			p1_branch1_items.add(factory.simple1("p1_branch1_item0", "param2"));
			p1_branch1_items.add(factory.simple2("p1_branch0_item1", 2));
			val p1_branch1 = new SequenceWorkflowAction("p1_branch1", p1_branch1_items);
			p1Items.put("branch1", p1_branch1);
		}
		{
			val p1_branch2 = factory.simple2("p1_branch2", 3);
			p1Items.put("branch2", p1_branch2);
		}
		val wf = factory.parallel("wf", p1Items);

		checkWorkflowAction(wf);
	}

	@Test
	public void testBuilder() {
		val factory = new WorkflowActionFactory();
		val builder = new WorkflowActionBuilder(factory);
		
		val wf = builder.parallel("wf", wfBuilder -> {
			wfBuilder.branch("branch0", "p1_branch0", branch0Builder -> {
				branch0Builder.addSimple1("p1_branch0_item0", "param1");
				branch0Builder.addSimple2("p1_branch0_item1", 1);
			});
			wfBuilder.branch("branch1", "p1_branch1", branch1Builder -> {
				branch1Builder.addSimple1("p1_branch1_item0", "param2");
				branch1Builder.addSimple2("p1_branch1_item1", 2);
			});
			wfBuilder.branch("branch2", "p1_branch2", branch2Builder -> {
				branch2Builder.addSimple2("p1_branch2", 3);
			});
		});

		checkWorkflowAction(wf);
	}

	@Test
	public void testDslFileSimple1() {
		val dslFile = new File("config/dsl-simple1.groovy");
		doParseDslFile(dslFile);
	}

	@Test
	public void testDslFileSimple2() {
		val dslFile = new File("config/dsl-simple2.groovy");
		doParseDslFile(dslFile);
	}

	@Test
	public void testDslFileSeq1() {
		val dslFile = new File("config/dsl-seq1.groovy");
		doParseDslFile(dslFile);
	}

	@Test
	public void testDslFileParallel1() {
		val dslFile = new File("config/dsl-parallel1.groovy");
		doParseDslFile(dslFile);
	}

	@Test
	public void testDslFileComplex() {
		val dslFile = new File("config/dsl-complex.groovy");
		doParseDslFile(dslFile);
	}

	
	private void doParseDslFile(File dslFile) {
		val factory = new WorkflowActionFactory();
		val builder = new WorkflowActionBuilder(factory);
		val dslHelper = new WorkflowActionDslHelper(builder);

		val wf = dslHelper.evalDsl(dslFile);

		checkWorkflowAction(wf);
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
