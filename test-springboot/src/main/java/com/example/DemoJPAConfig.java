package com.example;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages="com.example.repository")
@EntityScan(basePackages="com.example.domain")
public class DemoJPAConfig {

}
