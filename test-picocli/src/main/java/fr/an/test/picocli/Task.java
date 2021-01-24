package fr.an.test.picocli;

public abstract class Task {

	public abstract String displayName();
	public abstract void run(TaskContext ctx);
	
}