package fr.an.tests.testdslworkflowaction.ast;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;

public abstract class AbstractWorkflowAction {

	public final String name;
	
	public AbstractWorkflowAction(String name) {
		this.name = name;
	}

	public abstract void printIndent(PrintStream out, int indent);
	
	protected void indent(PrintStream out, int indent) {
		for(int i = 0; i < indent; i++) {
			out.print(' '); 
		}
	}
	
	public static class SequenceWorkflowAction extends AbstractWorkflowAction {
		@Getter
		List<AbstractWorkflowAction> items;

		public SequenceWorkflowAction(String name, List<AbstractWorkflowAction> items) {
			super(name);
			this.items = items;
		}

		@Override
		public void printIndent(PrintStream out, int indent) {
			indent(out, indent);
			out.print(name + ": Sequence " + items.size() + "\n");
			int indexIndent = indent + 1;
			int childIndent = indexIndent + 1;
			for(int i = 0; i < items.size(); i++) {
				indent(out, indexIndent);
				out.print("[" + i + "]\n");
				items.get(i).printIndent(out, childIndent);
			}
		}

	}

	public static class ParallelWorkflowAction extends AbstractWorkflowAction {
		Map<String,AbstractWorkflowAction> branchItems;

		public ParallelWorkflowAction(String name, Map<String,AbstractWorkflowAction> branchItems) {
			super(name);
			this.branchItems = branchItems;
		}

		@Override
		public void printIndent(PrintStream out, int indent) {
			indent(out, indent);
			out.print(name + ": Parallel " + branchItems.size() + "\n");
			int branchLabelIndent = indent + 1;
			int childIndent = branchLabelIndent + 1;
			for(val e : branchItems.entrySet()) {
				String branchName = e.getKey();
				indent(out, branchLabelIndent);
				out.print("branch [" + branchName + "]\n");
				e.getValue().printIndent(out, childIndent);
			}
		}

	}

	public static class Simple1WorkflowAction extends AbstractWorkflowAction {
		@Getter
		private String stringParam;

		public Simple1WorkflowAction(String name, String stringParam) {
			super(name);
			this.stringParam = stringParam;
		}
		
		@Override
		public void printIndent(PrintStream out, int indent) {
			indent(out, indent);
			out.print(name + ": Simple1{ stringParam:" + stringParam + " }\n");
		}
	}

	public static class Simple2WorkflowAction extends AbstractWorkflowAction {
		@Getter
		private int intParam;

		public Simple2WorkflowAction(String name, int intParam) {
			super(name);
			this.intParam = intParam;
		}

		@Override
		public void printIndent(PrintStream out, int indent) {
			indent(out, indent);
			out.print(name + ": Simple2{ intParam:" + intParam + " }\n");
		}

	}
	
}
