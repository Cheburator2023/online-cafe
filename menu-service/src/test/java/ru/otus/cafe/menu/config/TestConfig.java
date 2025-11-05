package ru.otus.cafe.menu.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;

import java.util.Collections;

@TestConfiguration
@EnableDiscoveryClient(autoRegister = false)
public class TestConfig {

    @Bean
    public SimpleDiscoveryClient simpleDiscoveryClient() {
        SimpleDiscoveryProperties properties = new SimpleDiscoveryProperties();
        properties.setInstances(Collections.emptyMap());
        return new SimpleDiscoveryClient(properties);
    }
}