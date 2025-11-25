package com.github.senocak.easyspec.integration.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@SpringBootApplication(scanBasePackages = "com.example.easyspec.integration")
@EnableJpaRepositories(basePackages = "com.example.easyspec.integration.repository")
@EntityScan(basePackages = "com.example.easyspec.integration.entity")
public class TestConfig {
}

