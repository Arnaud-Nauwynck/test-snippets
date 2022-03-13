
Several ways to create a in-memory tree for a given "workflow" AST classes: sequence/parallel/simple1/simple2
----


# Plain old Java code with new()

```
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
```

# Java code using Factory

```
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
```


# Using Java Configurer with lambdas

```
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
```

# Using Groovy DSL

```
  cfg.workflow {
  	parallel("wf") {
  		sequence("p1_branch0") {
  			simple1("p1_branch0_item0", "param1")
  			simple2("p1_branch0_item1", 1)
  		}
  		sequence("p1_branch1") {
  			simple1("p1_branch1_item0", "param2")
  			simple2("p1_branch1_item1", 2)
  		}
  		simple2("p1_branch2", 3)
  	}
  }
```

# Using Springboot Yaml config parsing

```
app:
  prop1: value1
  workflows:
    - type: parallel
      name: p1
      branchItems:
        - branchName: branch0
          item:
            type: sequence
            items: 
              - type: simple1
                name: p1_branch0_item0
                param: "param1"
              - type: simple2
                name: p1_branch0_item1
                intParam: 1
        - branchName: branch1
          item:
            type: sequence
            items: 
              - type: simple1
                name: p1_branch1_item0
                param: "param2"
              - type: simple2
                name: p1_branch1_item1
                intParam: 2
        - branchName: branch2
          item:
            type: simple2
            name: p1_branch2
            intParam: 3
```
