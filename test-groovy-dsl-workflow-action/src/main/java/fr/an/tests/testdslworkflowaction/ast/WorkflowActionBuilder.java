package fr.an.tests.testdslworkflowaction.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.ParallelWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.SequenceWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.Simple1WorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.Simple2WorkflowAction;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class WorkflowActionBuilder {

	@Getter
	private final WorkflowActionFactory factory;
	
	public WorkflowActionBuilder(WorkflowActionFactory factory) {
		this.factory = factory;
	}

	public SequenceWorkflowAction sequence(String name, Consumer<SequenceWorkflowActionConfigurer> configureCallback) {
		val configurer = new SequenceWorkflowActionConfigurer(this, name);
		configureCallback.accept(configurer);
		return configurer.build();
	}

	public ParallelWorkflowAction parallel(String name, Consumer<ParallelWorkflowActionConfigurer> configureCallback) {
		val configurer = new ParallelWorkflowActionConfigurer(this, name);
		configureCallback.accept(configurer);
		return configurer.build();
	}

	public Simple1WorkflowAction simple1(String name, String param) {
		return factory.simple1(name, param);
	}

	public Simple2WorkflowAction simple2(String name, int param) {
		return factory.simple2(name, param);
	}

	// ------------------------------------------------------------------------
	
	public static class SequenceWorkflowActionConfigurer {

		private final WorkflowActionBuilder builder;
		private final WorkflowActionFactory factory;
		@Getter @Setter
		private String name;
		
		List<AbstractWorkflowAction> items = new ArrayList<AbstractWorkflowAction>();

		public SequenceWorkflowActionConfigurer(WorkflowActionBuilder builder, String name) {
			this.builder = builder;
			this.name = name;
			this.factory = builder.factory;
		}
		
		public void addItem(AbstractWorkflowAction addItem) {
			if (addItem instanceof SequenceWorkflowAction) {
				val item2 = (SequenceWorkflowAction) addItem;
				this.items.addAll(item2.getItems());
			} else {
				this.items.add(addItem);
			}
		}

		public void addItems(Collection<AbstractWorkflowAction> addItems) {
			for(val addItem: addItems) {
				addItem(addItem);
			}
		}
		
		// add Sequence to Sequence.. useless
//		public WorkflowActionListConfigurer addSequence(Consumer<SequenceWorkflowActionConfigurer> configureCallback) {
//			val item = builder.sequence(name, configureCallback);
//			items.add(item);
//			return this;
//		}

		public SequenceWorkflowActionConfigurer addParallel(String name, Consumer<ParallelWorkflowActionConfigurer> configureCallback) {
			val item = builder.parallel(name, configureCallback);
			items.add(item);
			return this;
		}
		
		public SequenceWorkflowActionConfigurer addSimple1(String name, String param) {
			val res = factory.simple1(name, param);
			items.add(res);
			return this;
		}

		public SequenceWorkflowActionConfigurer addSimple2(String name, int param) {
			val res = factory.simple2(name, param);
			items.add(res);
			return this;
		}
		
		public SequenceWorkflowAction build() {
			return new SequenceWorkflowAction(name, items);
		}

	}

	
	// ------------------------------------------------------------------------
	
	public static class ParallelWorkflowActionConfigurer {
		private final WorkflowActionBuilder builder;
		private final WorkflowActionFactory factory;
		@Getter @Setter
		private String name;

		private final Map<String,SequenceWorkflowActionConfigurer> branchConfigurers = new LinkedHashMap<>();
		
		public ParallelWorkflowActionConfigurer(WorkflowActionBuilder builder, String name) {
			this.builder = builder;
			this.name = name;
			this.factory = builder.factory;
		}
		
		public ParallelWorkflowActionConfigurer branch(String branchName, String itemName, Consumer<SequenceWorkflowActionConfigurer> configureCallback) {
			val branchConfigurer = getOrCreateBranchConfigurer(branchName, itemName);
			configureCallback.accept(branchConfigurer);
			return this;
		}

		public SequenceWorkflowActionConfigurer getOrCreateBranchConfigurer(String branchName, String itemName) {
			SequenceWorkflowActionConfigurer branchConfigurer = branchConfigurers.get(branchName);
			if (branchConfigurer == null) {
				branchConfigurer = new SequenceWorkflowActionConfigurer(builder, itemName);
				branchConfigurers.put(branchName, branchConfigurer);
			}
			return branchConfigurer;
		}
		
		public ParallelWorkflowAction build() {
			val items = new LinkedHashMap<String,AbstractWorkflowAction>();
			for(val e : branchConfigurers.entrySet()) {
				String branchName = e.getKey();
				val branchConfigurer = e.getValue();
				SequenceWorkflowAction item = branchConfigurer.build();
				items.put(branchName, item);
			}
			return new ParallelWorkflowAction(name, items);
		}
		
	}
	
}
