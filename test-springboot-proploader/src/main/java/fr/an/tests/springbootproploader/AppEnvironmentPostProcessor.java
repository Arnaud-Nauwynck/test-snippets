package fr.an.tests.springbootproploader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;

// cf directly referenced in META-INF/spring.factories
public class AppEnvironmentPostProcessor implements EnvironmentPostProcessor {

	/**
	 * Post-process the given {@code environment}.
	 * @param environment the environment to post-process
	 * @param application the application to which the environment belongs
	 */
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment env,
				SpringApplication application) {
		MutablePropertySources propertySources = env.getPropertySources();
		try {
			propertySources.addLast(new ResourcePropertySource("file:src/data/order-propSource3.properties"));
			propertySources.addLast(new ResourcePropertySource("file:src/data/propSource1.properties"));
			propertySources.addLast(new ResourcePropertySource("file:src/data/order-propSource2.properties"));
		} catch(IOException ex) {
			throw new RuntimeException("", ex);
		}
		propertySources.addLast(mapPropSource());
		propertySources.addLast(customDynResource1Resolver());
	}

	// does not work?.. 
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer propSource1() {
//		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
//		pspc.setLocations(new org.springframework.core.io.Resource[] {
//				new FileSystemResource("src/data/order-propSource3.properties"),
//				new FileSystemResource("src/data/propSource1.properties"),
//				new FileSystemResource("src/data/order-propSource2.properties"), 
//		});
//		// pspc.setIgnoreUnresolvablePlaceholders(true);
//		return pspc;
//	}

	
	// does not work?.. 
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer propSource1() {
//		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
//		pspc.setLocations(new AbstractResource[] {
//				new FileSystemResource("src/data/propSource1.properties") 
//		});
//		// pspc.setIgnoreUnresolvablePlaceholders(true);
//		return pspc;
//	}
//	
//	@Order(100)
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer orderPropSource2() {
//		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
//		pspc.setLocations(new AbstractResource[] {
//				new FileSystemResource("src/data/order-propSource2.properties")
//		});
//		return pspc;
//	}
//
//	@Order(-100)
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer orderPropSource3() {
//		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
//		pspc.setLocations(new AbstractResource[] {
//				new FileSystemResource("src/data/order-propSource3.properties")
//		});
//		return pspc;
//	}

	
//	
//	@Bean
//	public static ResourcePropertySource propSource1() throws IOException {
//		return new ResourcePropertySource("propSource1", "file://src/data/propSource1.properties");
//	}
//
//	@Order(100)
//	@Bean
//	public static ResourcePropertySource orderPropSource2() throws IOException {
//		return new ResourcePropertySource("orderPropSource2", "file://src/data/order-propSource2.properties");
//	}
//
//	@Order(-100)
//	@Bean
//	public static ResourcePropertySource orderPropSource3() throws IOException {
//		return new ResourcePropertySource("orderPropSource3", "file://src/data/order-propSource3.properties");
//	}

	
	public static MapPropertySource mapPropSource() {
		Map<String,Object> map = new HashMap<>();
		map.put("app.mapPropSource-prop1", "mapPropSource().map.get(app.mapPropSource-prop1)");
		return new MapPropertySource("mapPropSource", map);
	}

	public static org.springframework.core.env.PropertySource<String> customDynResource1Resolver() {
		return new org.springframework.core.env.PropertySource<String>("customDynResource1Resolver") {
			@Override
			public boolean containsProperty(String name) {
				return name.startsWith("app.customDynResource1Resolver.");
			}

			@Override
			public Object getProperty(String name) {
				if (name.startsWith("app.customDynResource1Resolver.")) {
					return "customDynResource1Resolver.getProperty(" + name + ")";
				} else {
					return null;
					// otherwise ... => 
					//  org.springframework.boot.context.properties.bind.BindException: Failed to bind properties under 'spring.output.ansi.enabled' to org.springframework.boot.ansi.AnsiOutput$Enabled
					//  Caused by: java.lang.IllegalArgumentException: No enum constant org.springframework.boot.ansi.AnsiOutput.Enabled.customDynResource1Resolver.getProperty(spring.output.ansi.enabled)
				}
			}
		};
	}



}