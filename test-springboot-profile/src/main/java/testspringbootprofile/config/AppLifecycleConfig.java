package testspringbootprofile.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppLifecycleConfig {

	@Autowired
	private AppPlaceholderService placeholders;

	private static int appStartedCount = 0;
	private static int contextRefreshedCount = 0;
	private static int contextClosedCount = 0;
	private static int contextStartedCount = 0;
	private static int contextStoppedCount = 0;
	
	@EventListener
	public void onAppInit(ApplicationStartedEvent event) {
		System.out.println();
		System.out.println("########## app.started " + (++appStartedCount) + " " + event.getApplicationContext().getEnvironment());
		placeholders.dumpPlaceholders();
		System.out.println();
	}

	@EventListener
	public void onContextRefreshed(ContextRefreshedEvent event) {
		System.out.println();
		System.out.println("########## app.ctx refreshed " + (++contextRefreshedCount) + " " + event.getApplicationContext().getEnvironment());
		System.out.println();
	}

	@EventListener
	public void onContextClosed(ContextClosedEvent event) {
		System.out.println();
		System.out.println("########## app.ctx closed " + (++contextClosedCount) + " " + event.getApplicationContext().getEnvironment());
		System.out.println();
	}

	// NOT called !
	@EventListener
	public void onContext(ContextStartedEvent event) {
		System.out.println();
		System.out.println("########## app.ctx started " + (++contextStartedCount) + " " + event.getApplicationContext().getEnvironment());
		placeholders.dumpPlaceholders();
		System.out.println();
	}

	// NOT called !
	@EventListener
	public void onContextClosed(ContextStoppedEvent event) {
		System.out.println();
		System.out.println("########## app.ctx stopped " + (++contextStoppedCount) + " " + event.getApplicationContext().getEnvironment());
		System.out.println();
	}

}