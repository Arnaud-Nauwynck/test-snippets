package fr.an.tools.git2neo4j.config;

import java.io.File;

import org.neo4j.ogm.config.DriverConfiguration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories("fr.an.tools.git2neo4j.repository")
public class Neo4jConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(Neo4jConfig.class);
	
	@Configuration
	@ConditionalOnProperty(name="git2neo4j.neo4j.mode", havingValue="bolt")
	public static class Neo4jBoltConfig extends Neo4jConfiguration {
		@Value("${git2neo4j.neo4j.url}")
		private String neo4jURL;

		@Override
	    public SessionFactory getSessionFactory() {
			org.neo4j.ogm.config.Configuration neo4jConfig = new org.neo4j.ogm.config.Configuration();
	    	DriverConfiguration driverConf = neo4jConfig.driverConfiguration();
	    	driverConf.setDriverClassName(org.neo4j.ogm.drivers.bolt.driver.BoltDriver.class.getName());
	    	driverConf.setURI(neo4jURL);
	    	LOG.info("using neo4j mode bolt, url: " + neo4jURL);
			return new SessionFactory(neo4jConfig, "fr.an.tools.git2neo4j.domain");
		}
	
		public Session getSession() throws Exception {
			Session session = super.getSession();
			return session;
		}
	}
	
	
	@Configuration
	@ConditionalOnProperty(name="git2neo4j.neo4j.mode", havingValue="embedded")
	public static class Neo4jEmbeddedConfig extends Neo4jConfiguration {
	
		@Value("${neo4j.dbDir:src/test/neo4j.db}")
		private String neo4jDbDir;
		
	    @Override
	    public SessionFactory getSessionFactory() {
	    	org.neo4j.ogm.config.Configuration neo4jConfig = new org.neo4j.ogm.config.Configuration();
	    	DriverConfiguration driverConf = neo4jConfig.driverConfiguration();
			driverConf.setDriverClassName(org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver.class.getName());
			File neo4jDbDirFile = new File(neo4jDbDir); 
			if (! neo4jDbDirFile.exists()) {
				neo4jDbDirFile.mkdirs();
			}
			String uri = "file://" + neo4jDbDirFile.getAbsolutePath();
			driverConf.setURI(uri);
	    	LOG.info("using neo4j mode embedded, uri: " + uri);
	    	return new SessionFactory(neo4jConfig, "fr.an.tools.git2neo4j.domain");
	    }
	}
	
	
//    @Bean
//    @Override
//    public Neo4jServer neo4jServer() {
//        return new InProcessServer();
//    }

    
}

