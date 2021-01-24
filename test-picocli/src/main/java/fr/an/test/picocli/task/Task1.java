package fr.an.test.picocli.task;

import fr.an.test.picocli.Task;
import fr.an.test.picocli.TaskContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Task1 extends Task {

	public final String date;
	
	@Override
	public String displayName() {
		return "Task1(date=" + date + ")";
	}

	@Override
	public void run(TaskContext ctx) {
		System.out.println("running " + displayName());
	}
	
}
