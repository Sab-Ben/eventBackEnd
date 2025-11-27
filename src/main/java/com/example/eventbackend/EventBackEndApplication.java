package com.example.eventbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventBackEndApplication.class, args);
    }

}
