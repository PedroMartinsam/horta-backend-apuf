package com.apuf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApufApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApufApplication.class, args);
    }
}
