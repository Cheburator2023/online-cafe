package ru.otus.cafe.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@Profile({"!test", "default", "docker"})
public class LoadBalancerConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerConfig.class);

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(ConfigurableApplicationContext context) {
        logger.info("Configuring ServiceInstanceListSupplier for load balancing");
        return ServiceInstanceListSupplier.builder()
                .withBlockingDiscoveryClient()
                .withHealthChecks()
                .build(context);
    }

    @Bean
    @Profile("test")
    public ServiceInstanceListSupplier testServiceInstanceListSupplier() {
        return new ServiceInstanceListSupplier() {
            @Override
            public String getServiceId() {
                return "test-service";
            }

            @Override
            public Flux<List<ServiceInstance>> get() {
                return Flux.just(List.of(
                        new ServiceInstance() {
                            @Override
                            public String getInstanceId() {
                                return ServiceInstance.super.getInstanceId();
                            }

                            @Override
                            public String getServiceId() {
                                return "user-service";
                            }

                            @Override
                            public String getHost() {
                                return "localhost";
                            }

                            @Override
                            public int getPort() {
                                return 8000;
                            }

                            @Override
                            public boolean isSecure() {
                                return false;
                            }

                            @Override
                            public URI getUri() {
                                return URI.create("http://localhost:8000");
                            }

                            @Override
                            public Map<String, String> getMetadata() {
                                return Map.of();
                            }

                            @Override
                            public String getScheme() {
                                return ServiceInstance.super.getScheme();
                            }
                        }
                ));
            }
        };
    }
}