package fr.an.tests.testdslworkflowaction.groovydsl;

import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.SequenceWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionBuilder;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionBuilder.ParallelWorkflowActionConfigurer;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionBuilder.SequenceWorkflowActionConfigurer;
import groovy.lang.Closure;
import lombok.val;

public class WorkflowActionBuilderDsl {
	
	private final WorkflowActionBuilder builder;

	private TopLevelWorkflowActionDsl workflow = new TopLevelWorkflowActionDsl();
	
	/*pp*/ AbstractWorkflowAction getWorkflowItem() {
		return workflow.item;
	}
	
	// ------------------------------------------------------------------------
	
	public WorkflowActionBuilderDsl(WorkflowActionBuilder builder) {
		super();
		this.builder = builder;
	}
	
	
	// ------------------------------------------------------------------------
	
	
	public void workflow(Closure<?> closure) {
		closure.setDelegate(workflow);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();
	}
	
	
	public class TopLevelWorkflowActionDsl extends AbstractWorkflowActionAddDsl {
		AbstractWorkflowAction item;
		
		@Override
		protected  void add(AbstractWorkflowAction item) {
			this.item = item;
		}
		
	}
	
	public class SequenceWorkflowActionDsl extends AbstractWorkflowActionAddDsl {
		SequenceWorkflowActionConfigurer configurer;
		
		public SequenceWorkflowActionDsl(SequenceWorkflowActionConfigurer configurer) {
			this.configurer = configurer;
		}

		@Override
		protected void add(AbstractWorkflowAction item) {
			if (item instanceof SequenceWorkflowAction) {
				val item2 = (SequenceWorkflowAction) item;
				configurer.addItems(item2.getItems());
			} else {
				configurer.addItem(item);
			}
		}
		
	}
	
	public class ParallelWorkflowActionDsl extends AbstractWorkflowActionAddDsl {
		ParallelWorkflowActionConfigurer configurer;
		int branchIndexCount;
		
		public ParallelWorkflowActionDsl(ParallelWorkflowActionConfigurer configurer) {
			this.configurer = configurer;
		}

		@Override
		protected void add(AbstractWorkflowAction item) {
			int branchIndex = branchIndexCount++;
			String branchName = "branch" + branchIndex;
			val branchConfigurer = configurer.getOrCreateBranchConfigurer(branchName, branchName);
			branchConfigurer.addItem(item);
		}
		
	}
	
	
	protected abstract class AbstractWorkflowActionAddDsl {
		protected abstract void add(AbstractWorkflowAction item);
		
		public void sequence(String name, Closure<?> configureClosure) {
			val seqConfigurer = new SequenceWorkflowActionConfigurer(builder, name);
			val delegateDsl = new SequenceWorkflowActionDsl(seqConfigurer);
			configureClosure.setDelegate(delegateDsl);
			configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
			configureClosure.call();
			val item = seqConfigurer.build();
			add(item);
		}

		public void parallel(String name, Closure<?> configureClosure) {
			val configurer = new ParallelWorkflowActionConfigurer(builder, name);
			val delegateDsl = new ParallelWorkflowActionDsl(configurer);
			configureClosure.setDelegate(delegateDsl);
			configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
			configureClosure.call();
			val item = configurer.build();
			add(item);
		}

		public void simple1(String name, String param) {
			val item = builder.getFactory().simple1(name, param);
			add(item);
		}

		public void simple2(String name, int param) {
			val item = builder.getFactory().simple2(name, param);
			add(item);
		}
		
	}

}
