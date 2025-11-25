package com.github.senocak.easyspec.integration.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@SpringBootApplication(scanBasePackages = "com.github.senocak.easyspec.integration")
@EnableJpaRepositories(basePackages = "com.github.senocak.easyspec.integration.repository")
@EntityScan(basePackages = "com.github.senocak.easyspec.integration.entity")
public class TestConfig {
}

