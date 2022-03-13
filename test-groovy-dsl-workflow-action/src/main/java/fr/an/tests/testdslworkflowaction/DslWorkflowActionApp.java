package fr.an.tests.testdslworkflowaction;

import java.io.File;

import fr.an.tests.testdslworkflowaction.ast.WorkflowActionBuilder;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionFactory;
import fr.an.tests.testdslworkflowaction.groovydsl.WorkflowActionDslHelper;
import lombok.val;

public class DslWorkflowActionApp {

	public static void main(String[] args) {
		val dslFile = new File("config/dsl.groovy");

		val factory = new WorkflowActionFactory();
		val builder = new WorkflowActionBuilder(factory);
		val dslHelper = new WorkflowActionDslHelper(builder);
		
		val res = dslHelper.evalDsl(dslFile);
		
		System.out.println("dsl => ");
		res.printIndent(System.out, 0);
	}

}
