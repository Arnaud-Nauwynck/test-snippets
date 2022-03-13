package fr.an.tests.testdslworkflowaction.ast;

import java.util.List;
import java.util.Map;

import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.ParallelWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.SequenceWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.Simple1WorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction.Simple2WorkflowAction;

public class WorkflowActionFactory {

	public SequenceWorkflowAction sequence(String name, List<AbstractWorkflowAction> items) {
		return new SequenceWorkflowAction(name, items);
	}

	public ParallelWorkflowAction parallel(String name, Map<String,AbstractWorkflowAction> branchItems) {
		return new ParallelWorkflowAction(name, branchItems);
	}

	public Simple1WorkflowAction simple1(String name, String param) {
		return new Simple1WorkflowAction(name, param);
	}

	public Simple2WorkflowAction simple2(String name, int param) {
		return new Simple2WorkflowAction(name, param);
	}

}
