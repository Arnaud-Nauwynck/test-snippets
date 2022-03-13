
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
