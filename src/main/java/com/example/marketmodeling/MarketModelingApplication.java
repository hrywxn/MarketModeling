package com.example.marketmodeling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MarketModelingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketModelingApplication.class, args);
    }

}
