package fr.an.tests.springbootproploader;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class PropLoaderApp {

	public static void main(String[] args) {
		SpringApplication.run(PropLoaderApp.class, args);
	}
	
}


@Component
class AppCommandLineRunner implements CommandLineRunner {
	@Autowired private AppFoo foo;
	
	public void run(String... args) throws Exception {
		foo.dumpProps();
	}
	
}


@Configuration
@PropertySource("classpath:foo.properties") // => load from "src/main/resources/foo.properties" (or src/test/classes/foo.properties in test mode)
@PropertySource("classpath:foo-${app.env}.properties") // => load "src/main/resources/foo-test.properties"
@PropertySource("file:src/data/data.properties") // => load from "src/main/data/data.properties"
class AppResolverConfiguration {
	
	@Bean
	public static PropertyOverrideConfigurer propertyOverrideConfigurer() {
		PropertyOverrideConfigurer bean = new PropertyOverrideConfigurer();
		bean.setLocalOverride(true);
		bean.setLocation(new FileSystemResource(new File("src/data/bean-override.properties")));
		return bean;
	}

	@Bean
	public static PropertyOverrideConfigurer propertyOverrideFalseConfigurer() {
		PropertyOverrideConfigurer bean = new PropertyOverrideConfigurer();
		// bean.setLocalOverride(false); .. default
		bean.setLocation(new FileSystemResource(new File("src/data/bean-override-false.properties")));
		return bean;
	}

}

@Component
class AppFoo {

	private static final Logger log = LoggerFactory.getLogger(AppFoo.class);

	@Value("${app.bootstrap-prop1}") private String appBootstrapProp1;
//	@Value("${app.activeprofile2-prop1}") private String appActiveProfile2Prop1;
	
	@Value("${app.bootprofile1-prop1}") private String bootprofile1Prop1;
	@Value("${app.bootprofile2-prop1}") private String bootprofile2Prop1;
	@Value("${app.bootprofile3-prop1}") private String bootprofile3Prop1;
	@Value("${app.bootprofile4-prop1}") private String bootprofile4Prop1;
	@Value("${app.bootprofile1a-prop1}") private String bootprofile1aProp1;
	
	@Value("${app.key1}") private String appKey1;
	@Value("${app.key2}") private String appKey2;

	@Value("${app.classpath-resource-prop1}") private String appClasspathResourceProp1;
	@Value("${app.classpath-dyn-resource-prop1}") private String appClasspathDynResourceProp1;
	@Value("${app.file-data-prop1}") private String appFileResourceProp1;

	@Value("${user.home}") private String systemPropUserHome;
	@Value("${HOME}") private String systemEnvVariableHOME;

	@Value("${app.propSource1-prop1}") private String appPropSource1Prop1; 
	@Value("${app.orderPropSource2-prop1}") private String appOrderPropSource2Prop1; 
	@Value("${app.orderPropSource3-prop1}") private String appOrderPropSource3Prop1; 
	@Value("${app.mapPropSource-prop1}") private String appMapPropSourceProp1;
	@Value("${app.customDynResource1Resolver.prop1}") private String appCustomDynResource1ResolverProp1; 
	
	@Value("${app.runListenerMapPropSource-prop1}") private String appRunListenerMapPropSourceProp1;
	
	@Value("${app.fieldOverrideTrueNull:null}")
	private String fieldOverrideTrueNull;
	@Value("${app.fieldOverrideTrueProp1:default}")
	private String fieldOverrideTrueProp1 = "value1";
	
	@Value("${app.fieldOverrideFalseNull:null}")
	private String fieldOverrideFalseNull;
	@Value("${app.fieldOverrideFalseProp1:default}")
	private String fieldOverrideFalseProp1 = "value1";
	
	public AppFoo() {
		log.info("AppFoo");
	}

	@PostConstruct
	public void init() {
		log.info("AppFoo.init");
	}

	public String getFieldOverrideTrueNull() {
		return fieldOverrideTrueNull;
	}

	public void setFieldOverrideTrueNull(String fieldOverrideTrueNull) {
		log.info("appFoo.setFieldOverrideTrueNull");
		this.fieldOverrideTrueNull = fieldOverrideTrueNull;
	}

	public String getFieldOverrideFalseNull() {
		return fieldOverrideFalseNull;
	}

	public void setFieldOverrideFalseNull(String fieldOverrideFalseNull) {
		log.info("appFoo.setFieldOverrideFalseNull");
		this.fieldOverrideFalseNull = fieldOverrideFalseNull;
	}

	public String getFieldOverrideFalseProp1() {
		return fieldOverrideFalseProp1;
	}

	public void setFieldOverrideFalseProp1(String fieldOverrideFalseProp1) {
		log.info("appFoo.setFieldOverrideFalseProp1");
		this.fieldOverrideFalseProp1 = fieldOverrideFalseProp1;
	}

	public String getFieldOverrideTrueProp1() {
		return fieldOverrideTrueProp1;
	}

	public void setFieldOverrideTrueProp1(String fieldOverrideTrueProp1) {
		log.info("appFoo.setFieldOverrideTrueProp1");
		this.fieldOverrideTrueProp1 = fieldOverrideTrueProp1;
	}

	

	
	public void dumpProps() {
		log.info("dumpProps:\n" + 
				"app.bootstrap-prop1: " + appBootstrapProp1 + "  ..  cf bootstrap.yml \n" +
				"app.bootprofile1Prop1: " + bootprofile1Prop1 + " .. cf bootstrap.yml -> spring.profiles.active -> application-bootprofile1.yml\n" +
//				"app.activeProfile2Prop1:" + appActiveProfile2Prop1 + " .. cf \n" + 
				"app.bootprofile2Prop1: " + bootprofile2Prop1 + " .. cf bootstrap.yml -> spring.profiles.active -> application-bootprofile2.yml\n" +
				"app.bootprofile3Prop1: " + bootprofile3Prop1 + " .. cf application.yml -> spring.profiles.active not loaded ?? -> application.yml\n" +
				"app.bootprofile4Prop1: " + bootprofile4Prop1 + " .. cf application.yml -> spring.profiles.active not loaded ?? -> application.yml\n" +
				"app.bootprofile1aProp1: " + bootprofile1aProp1 + " .. cf bootstrap.yml -> application-bootprofile1.yml -> spring.profiles.include -> application-bootprofile1a.yml\n" +
				"app.key1: " + appKey1 + "\n" + 
				"app.key2: " + appKey2 + "\n" +
				"app.classpath-resource-prop1: " + appClasspathResourceProp1 + " .. cf @PropertySource(\"classpath:foo.properties\")\n" +
				"app.classpath-dyn-resource-prop1: " + appClasspathDynResourceProp1 + " .. cf @PropertySource(\"classpath:foo-${app.env}.properties\")\n" +
				"app.file-data-prop1: " + appFileResourceProp1 + " .. cf @PropertySource(\"file:src/data/data.properties\") \n" +

				"user.home:" + systemPropUserHome + " .. cf -D system prop \n" +
				"${HOME}:" + systemEnvVariableHOME + " .. cf export environment variables\n" +

				"app.propSource1-prop1: " + appPropSource1Prop1 + "  .. cf @Bean propSource1()\n" +
				"app.orderPropSource2-prop1: " + appOrderPropSource2Prop1 + "  .. cf @Bean propSource2() .. override propSource1 @Order() \n" +
				"app.orderPropSource3-prop2: " + appOrderPropSource3Prop1 + "  .. cf @Bean propSource3() .. does not override propSource1 \n" +
				"app.mapPropSource-prop1: " + appMapPropSourceProp1 + " .. cf @Bean mapPropertySource()\n" +
				"app.customDynResource1Resolver.prop1" + appCustomDynResource1ResolverProp1 + " .. cf @Bean customDynResource1Resolver()\n" +
				
				"app.runListenerMapPropSource-prop1:" + appRunListenerMapPropSourceProp1 + " .. cf AppRunListenerMapPropSource + MapSource.. \n" +
				
				"appFoo.fieldOverrideTrueNull:" + fieldOverrideTrueNull + "  .. cf @Bean fieldOverrideProp1().. new PropertyOverrideConfigurer(), location:src/data/bean-override.properties localOverride:true\n" +
				"appFoo.fieldOverrideTrueProp1:" + fieldOverrideTrueProp1 + "  .. cf @Bean fieldOverrideProp1().. new PropertyOverrideConfigurer(), location:src/data/bean-override.properties localOverride:true\n" +
				"appFoo.fieldOverrideFalseNull:" + fieldOverrideFalseNull + "  .. cf @Bean fieldOverrideProp2().. new PropertyOverrideConfigurer(), location:src/data/bean-override-false.properties localOverride:false\n" +
				"appFoo.fieldOverrideFalseProp1:" + fieldOverrideFalseProp1 + "  .. cf @Bean fieldOverrideProp2().. new PropertyOverrideConfigurer(), location:src/data/bean-override-false.properties localOverride:false\n"
				);
	}
	
}
