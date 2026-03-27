package com.shk.observabilitydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ObservabilityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityDemoApplication.class, args);
    }

}
