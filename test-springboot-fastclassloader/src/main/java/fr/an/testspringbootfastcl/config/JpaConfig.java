package fr.an.testspringbootfastcl.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages="fr.an.testspringbootfastcl.repository")
@EntityScan(basePackages="fr.an.testspringbootfastcl.domain")
public class JpaConfig {

}
