package com.example.txdxai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestTxdxaiApplication {

//    @Bean
//    @ServiceConnection
//    PostgreSQLContainer<?> postgresContainer() {
//        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
//    }
//    public static void main(String[] args) {
//        SpringApplication.from(TxdxaiApplication::main).with(TestTxdxaiApplication.class).run(args);
//    }
}
