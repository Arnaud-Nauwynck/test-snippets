package fr.an.tests.testazuremon;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;

import io.micrometer.azuremonitor.AzureMonitorMeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MeterRegistry.Config;

@SpringBootApplication
public class AzureMonMain {

    public static void main(String[] args) {
	SpringApplication.run(AzureMonMain.class, args);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    	return registry -> {
    	    Config config = registry.config();
	    config.commonTags("app", "test-azure-monitor");
	    config.commonTags("env", "DEV");
    	};
    }
}

@Component
class AppCmdLineRunner implements CommandLineRunner {
    @Autowired AzureMonitorMeterRegistry azureRegistry;
    @Autowired TelemetryConfiguration telemetryConfiguration;
    TelemetryClient telemetryClient;
    
    MeterRegistry registry;
    Counter testCount1;
    
    public AppCmdLineRunner(MeterRegistry registry) {
	this.registry = registry;
	this.testCount1 = registry.counter("test-count1", "tag1", "tag2");
    }

    @PostConstruct
    public void init() {
	telemetryClient = new TelemetryClient(telemetryConfiguration);
	
    }
    
    public void run(String... args) throws Exception {
	testCount1.increment();
	
	for(int i = 0; i < 50; i++) {
	    Map<String,String> props = new HashMap<String,String>();
	    props.put("app", "test-azure-monitor");
	    props.put("env", "DEV");
	    telemetryClient.trackEvent("test-event-" + i, props, null);
	}
	telemetryClient.flush();
	
	// azureRegistry.
	Thread.sleep(10_000);
	azureRegistry.close(); // => flush publish...
    }
    
}