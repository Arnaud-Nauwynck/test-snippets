package fr.an.test.picocli;

import java.util.ArrayList;
import java.util.List;

import fr.an.test.picocli.PicocliMain.SubCommand1;
import fr.an.test.picocli.task.Task1;
import lombok.val;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
		subcommands = {
				SubCommand1.class 
		}
		)
public class PicocliMain {

	private List<Task> tasks = new ArrayList<>();
	void addTask(Task t) {
		tasks.add(t);
	}
	
	protected static abstract class AbstractSubCommand implements Runnable {
		@ParentCommand protected PicocliMain parent;
		void addTask(Task t) {
			parent.addTask(t);
		}	
	}
	
	@Command(name = "command1", aliases = "cmd1")
	public static class SubCommand1 extends AbstractSubCommand {
		@Option(names = "--date") public String date;

		@Override
		public void run() {
			System.out.println("detected command1, date=" + date + " .. adding Task1");
			addTask(new Task1(date));
		}
	}

	@Command(name = "command2", aliases = "cmd2")
	public void command2(
		@Option(names = "--date") String date
		) {
		System.out.println("detected command2, date=" + date + " .. adding Task1");
		addTask(new Task1(date));
	}

	
	public static void main(String[] args) {
		new PicocliMain().run(args);
	}

	public void run(String[] args) {
		List<String> splitArgs = new ArrayList<>();
		for(val arg : args) {
			if (arg.equals("\\;")) {
				new CommandLine(this).execute(splitArgs.toArray(new String[splitArgs.size()]));
				splitArgs.clear();
			} else {
				splitArgs.add(arg);
			}
		}
		if (! splitArgs.isEmpty()) {
			new CommandLine(this).execute(splitArgs.toArray(new String[splitArgs.size()]));
		}
		
		
		int tasksCount = tasks.size();
		System.out.println("tasks: " + tasksCount);
		int index = 0;
		for(val t : tasks) {
			System.out.println("[" + (++index) + "/" + tasksCount + "] " + t.displayName());
		}
	}
	
}
