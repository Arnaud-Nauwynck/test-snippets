package fr.an.tests.testdslworkflowaction.groovydsl;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import fr.an.tests.testdslworkflowaction.ast.AbstractWorkflowAction;
import fr.an.tests.testdslworkflowaction.ast.WorkflowActionBuilder;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.val;

public class WorkflowActionDslHelper {

	private WorkflowActionBuilder builder;
	private GroovyShell groovyShell;

	
	public WorkflowActionDslHelper(WorkflowActionBuilder builder) {
		this.builder = builder;
		this.groovyShell = new GroovyShell();
	}

	public Script parseFile(File file) {
		try {
			return groovyShell.parse(file);
		} catch (CompilationFailedException ex) {
			throw new RuntimeException("", ex);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
	}

	public AbstractWorkflowAction evalDsl(String dslScriptText) {
		val script = groovyShell.parse(dslScriptText);
		return evalDsl(script);
	}
	
	public AbstractWorkflowAction evalDsl(File dslScriptFile) {
		val script = parseFile(dslScriptFile);
		return evalDsl(script);
	}
	
	public AbstractWorkflowAction evalDsl(Script dslScript) {
		WorkflowActionBuilderDsl dslImpl = new WorkflowActionBuilderDsl(builder);
		
		val binding = new Binding();
		binding.setVariable("cfg", dslImpl);
		dslScript.setBinding(binding);
		
//		MetaClass metaClass = dslScript.getMetaClass();
//		if (metaClass instanceof MetaClassImpl) {
//			MetaClassImpl mc = (MetaClassImpl) metaClass;
//			mc.addMetaMethod(null);
//		}
//		List<MetaMethod> metaMethods = metaClass.getMetaMethods();
		
		// val metaClass = new DelegatingMetaClass(WorkflowActionBuilderDsl.class);
		// dslScript.setMetaClass(metaClass);
		
		dslScript.run();
		
		return dslImpl.getWorkflowItem();
	}

}
